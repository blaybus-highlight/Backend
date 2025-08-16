package com.highlight.highlight_backend.service;

import com.highlight.highlight_backend.domain.User;
import com.highlight.highlight_backend.dto.PhoneVerificationRequestCodeDto;
import com.highlight.highlight_backend.dto.PhoneVerificationRequestDto;
import com.highlight.highlight_backend.dto.UserDetailResponseDto;
import com.highlight.highlight_backend.dto.UserLoginRequestDto;
import com.highlight.highlight_backend.dto.UserLoginResponseDto;
import com.highlight.highlight_backend.dto.UserSignUpRequestDto;
import com.highlight.highlight_backend.exception.BusinessException;
import com.highlight.highlight_backend.exception.ErrorCode;
import com.highlight.highlight_backend.repository.user.UserRepository;
import com.highlight.highlight_backend.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.nurigo.java_sdk.api.Message;
import net.nurigo.java_sdk.exceptions.CoolsmsException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Random;

/**
 * User 회원가입, 로그인 기능 // 휴대폰 SMS 인증 포함
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    
    @Value("${coolsms.api.key}")
    private String apiKey;

    @Value("${coolsms.api.secret}")
    private String apiSecret;

    @Value("${coolsms.from-number}")
    private String fromPhoneNumber;


    @Transactional
    public UserSignUpRequestDto signUp(UserSignUpRequestDto signUpRequestDto) {
        // 1. 중복 검사
        if (userRepository.existsByUserId(signUpRequestDto.getUserId())) {
            throw new BusinessException(ErrorCode.DUPLICATE_USER_ID);
        }
        if (userRepository.existsByNickname(signUpRequestDto.getNickname())) {
            throw new BusinessException(ErrorCode.DUPLICATE_NICKNAME);
        }
        if (userRepository.existsByPhoneNumber(signUpRequestDto.getPhoneNumber())) {
            throw new BusinessException(ErrorCode.DUPLICATE_PHONE_NUMBER);
        }

        // 2. 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(signUpRequestDto.getPassword());

        // 3. User 엔티티 생성 및 저장
        User user = new User();
        user.setUserId(signUpRequestDto.getUserId());
        user.setPassword(encodedPassword);
        user.setNickname(signUpRequestDto.getNickname());
        user.setPhoneNumber(signUpRequestDto.getPhoneNumber());
        user.setPhoneVerified(false);
        user.setOver14(signUpRequestDto.getIsOver14());
        user.setAgreedToTerms(signUpRequestDto.getAgreedToTerms());
        user.setMarketingEnabled(signUpRequestDto.getMarketingEnabled());
        user.setEventSnsEnabled(signUpRequestDto.getEventSnsEnabled());

        userRepository.save(user);
        return signUpRequestDto;
    }

    @Transactional
    public void requestPhoneVerification(PhoneVerificationRequestCodeDto requestDto) {
        User user = userRepository.findByPhoneNumber(requestDto.getPhoneNumber())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        String verificationCode = createRandomCode();
        user.setVerificationCode(verificationCode);
        user.setVerificationCodeExpiresAt(LocalDateTime.now().plusMinutes(3)); // 3분 후 만료
        userRepository.save(user);
        
        // SMS 키 적용
        Message coolsms = new Message(apiKey, apiSecret);
        HashMap<String, String> params = new HashMap<>();
        params.put("to", user.getPhoneNumber());
        params.put("from", fromPhoneNumber);
        params.put("type", "SMS");
        params.put("text", "[Highlight] 인증번호 [" + verificationCode + "]를 입력해주세요.");
        // SMS 요청
        try {
            coolsms.send(params);
        } catch (CoolsmsException e) {
            log.error("SMS 발송 실패: {}", e.getMessage());
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR); // 혹은 SMS 발송 관련 에러 코드
        }

        log.info("Generated verification code for {}: {}", user.getPhoneNumber(), verificationCode);
    }

    @Transactional
    public void verifyPhoneNumber(PhoneVerificationRequestDto requestDto) {
        User user = userRepository.findByPhoneNumber(requestDto.getPhoneNumber())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        if (user.getVerificationCode() == null || user.getVerificationCodeExpiresAt() == null) {
            throw new BusinessException(ErrorCode.VERIFICATION_CODE_NOT_MATCH); // Or a more specific error
        }

        if (LocalDateTime.now().isAfter(user.getVerificationCodeExpiresAt())) {
            throw new BusinessException(ErrorCode.VERIFICATION_CODE_NOT_MATCH); // Or a more specific "code expired" error
        }

        if (!user.getVerificationCode().equals(requestDto.getVerificationCode())) {
            throw new BusinessException(ErrorCode.VERIFICATION_CODE_NOT_MATCH);
        }

        user.setPhoneVerified(true);
        user.setVerificationCode(null);
        user.setVerificationCodeExpiresAt(null);
        userRepository.save(user);
    }

    private String createRandomCode() {
        Random random = new Random();
        int code = 100000 + random.nextInt(900000); // 100000 ~ 999999
        return String.valueOf(code);
    }

    public UserLoginResponseDto login(UserLoginRequestDto loginRequestDto) {
        // 1. 사용자 조회
        User user = userRepository.findByUserId(loginRequestDto.getUser_id())
                .orElseThrow(() -> new BusinessException(ErrorCode.INVALID_LOGIN_CREDENTIALS));

        // 2. 비밀번호 검증
        if (!passwordEncoder.matches(loginRequestDto.getPassword(), user.getPassword())) {
            throw new BusinessException(ErrorCode.INVALID_LOGIN_CREDENTIALS);
        }

        // 3. JWT 토큰 생성
        String accessToken = jwtUtil.generateAccessToken(user.getId(), user.getUserId(), "USER");
        String refreshToken = jwtUtil.generateRefreshToken(user.getId(), user.getUserId(), "USER");

        // 4. UserResponseDto 생성 및 반환
        return UserLoginResponseDto.builder()
                .user_id(user.getUserId())
                .nickname(user.getNickname())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    public UserDetailResponseDto getUserDetailsById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        return UserDetailResponseDto.from(user);
    }

    @Transactional
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        userRepository.delete(user);
    }
}
