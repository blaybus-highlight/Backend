package com.highlight.highlight_backend.service;

import com.highlight.highlight_backend.domain.PhoneVerification;
import com.highlight.highlight_backend.domain.User;
import com.highlight.highlight_backend.dto.PhoneVerificationRequestCodeDto;
import com.highlight.highlight_backend.dto.PhoneVerificationRequestDto;
import com.highlight.highlight_backend.dto.UserDetailResponseDto;
import com.highlight.highlight_backend.dto.UserLoginRequestDto;
import com.highlight.highlight_backend.dto.UserLoginResponseDto;
import com.highlight.highlight_backend.dto.UserSignUpRequestDto;
import com.highlight.highlight_backend.dto.MyPageResponseDto;
import com.highlight.highlight_backend.exception.BusinessException;
import com.highlight.highlight_backend.exception.UserErrorCode;
import com.highlight.highlight_backend.exception.SmsErrorCode;
import com.highlight.highlight_backend.repository.PhoneVerificationRepository;
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
    private final PhoneVerificationRepository phoneVerificationRepository;
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
            throw new BusinessException(UserErrorCode.DUPLICATE_USER_ID);
        }
        if (userRepository.existsByNickname(signUpRequestDto.getNickname())) {
            throw new BusinessException(UserErrorCode.DUPLICATE_NICKNAME);
        }
        if (userRepository.existsByPhoneNumber(signUpRequestDto.getPhoneNumber())) {
            throw new BusinessException(UserErrorCode.DUPLICATE_PHONE_NUMBER);
        }
        String phoneNumber = signUpRequestDto.getPhoneNumber();
        PhoneVerification verification = phoneVerificationRepository.findById(phoneNumber)
                .orElseThrow(() -> new BusinessException(UserErrorCode.VERIFICATION_REQUIRED)); // 인증 요청 기록 없음

        // 인증 완료 상태가 아니거나, 인증 유효 시간이 만료된 경우
        if (!verification.isVerified() || LocalDateTime.now().isAfter(verification.getExpiresAt())) {
            throw new BusinessException(SmsErrorCode.VERIFICATION_FAILED_OR_EXPIRED); // 인증 실패 또는 만료
        }

        // 2. 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(signUpRequestDto.getPassword());

        // 3. User 엔티티 생성 및 저장
        User user = new User();
        user.setUserId(signUpRequestDto.getUserId());
        user.setPassword(encodedPassword);
        user.setNickname(signUpRequestDto.getNickname());
        user.setPhoneNumber(signUpRequestDto.getPhoneNumber());
        user.setPhoneVerified(true); // 휴대폰 인증 완료 상태로 설정
        user.setOver14(signUpRequestDto.getIsOver14());
        user.setAgreedToTerms(signUpRequestDto.getAgreedToTerms());
        user.setMarketingEnabled(signUpRequestDto.getMarketingEnabled());
        user.setEventSnsEnabled(signUpRequestDto.getEventSnsEnabled());

        userRepository.save(user);
        return signUpRequestDto;
    }

    /**
     * 
     * 휴대폰 번호를 확인하고 SMS 요청을 보냄
     */
    @Transactional
    public void requestVerificationForSignUp(PhoneVerificationRequestCodeDto requestDto) {
        String phoneNumber = requestDto.getPhoneNumber();

        // 이미 가입된 번호인지 확인
        if (userRepository.existsByPhoneNumber(phoneNumber)) {
            throw new BusinessException(UserErrorCode.PHONE_NUMBER_ALREADY_EXISTS);
        }

        String verificationCode = createRandomCode();

        // DB에 인증번호와 만료 시간 저장 (기존에 있어도 덮어쓰기)
        PhoneVerification verification = new PhoneVerification(phoneNumber, verificationCode);
        phoneVerificationRepository.save(verification);

        // SMS 발송 로직 (수정: user 객체 대신 phoneNumber 변수 사용)
        Message coolsms = new Message(apiKey, apiSecret);
        HashMap<String, String> params = new HashMap<>();
        params.put("to", phoneNumber);
        params.put("from", fromPhoneNumber);
        params.put("type", "SMS");
        params.put("text", "nafal 회원가입 인증번호 " + verificationCode + " 를 입력해주세요.");

        try {
            coolsms.send(params);
        } catch (CoolsmsException e) {
            log.error("SMS 발송 실패: {}", e.getMessage());
            throw new BusinessException(SmsErrorCode.SMS_SEND_FAILED);
        }

        log.info("회원가입용 인증번호 발송: {}", phoneNumber);
    }

    /**
     * 입력된 인증번호가 유효한지 확인하는 메소드
     */
    @Transactional
    public void confirmVerification(PhoneVerificationRequestDto requestDto) {
        // DB에서 휴대폰 번호로 인증 정보를 조회
        PhoneVerification verification = phoneVerificationRepository.findById(requestDto.getPhoneNumber())
                .orElseThrow(() -> new BusinessException(SmsErrorCode.VERIFICATION_CODE_NOT_FOUND));

        // 만료 시간 확인
        if (LocalDateTime.now().isAfter(verification.getExpiresAt())) {
            throw new BusinessException(SmsErrorCode.VERIFICATION_CODE_EXPIRED);
        }

        // 인증번호 일치 여부 확인
        if (!verification.getVerificationCode().equals(requestDto.getVerificationCode())) {
            throw new BusinessException(UserErrorCode.VERIFICATION_CODE_NOT_MATCH);
        }

        // 인증 성공 시, verified 상태를 true로 변경하고 저장
        verification.setVerified(true);
        phoneVerificationRepository.save(verification);
    }

    private String createRandomCode() {
        Random random = new Random();
        int code = 100000 + random.nextInt(900000); // 100000 ~ 999999
        return String.valueOf(code);
    }

    public UserLoginResponseDto login(UserLoginRequestDto loginRequestDto) {
        // 1. 사용자 조회
        User user = userRepository.findByUserId(loginRequestDto.getUserId())
                .orElseThrow(() -> new BusinessException(UserErrorCode.INVALID_LOGIN_CREDENTIALS));

        // 2. 비밀번호 검증
        if (!passwordEncoder.matches(loginRequestDto.getPassword(), user.getPassword())) {
            throw new BusinessException(UserErrorCode.INVALID_LOGIN_CREDENTIALS);
        }

        // 3. JWT 토큰 생성
        String accessToken = jwtUtil.generateAccessToken(user.getId(), user.getUserId(), "USER");
        String refreshToken = jwtUtil.generateRefreshToken(user.getId(), user.getUserId(), "USER");

        // 4. UserResponseDto 생성 및 반환
        return UserLoginResponseDto.builder()
                .userId(user.getUserId())
                .nickname(user.getNickname())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    public UserDetailResponseDto getUserDetailsById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));
        return UserDetailResponseDto.from(user);
    }

    @Transactional
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));
        userRepository.delete(user);
    }
    
    /**
     * 마이페이지 정보 조회
     * 
     * @param userId 사용자 ID
     * @return 마이페이지 정보
     */
    @Transactional(readOnly = true)
    public MyPageResponseDto getMyPageInfo(Long userId) {
        log.info("마이페이지 정보 조회: 사용자ID={}", userId);
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));
        
        return MyPageResponseDto.from(user);
    }
}
