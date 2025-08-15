package com.highlight.highlight_backend.service;

import com.highlight.highlight_backend.domain.User;
import com.highlight.highlight_backend.dto.UserSignUpRequestDto;
import com.highlight.highlight_backend.exception.BusinessException;
import com.highlight.highlight_backend.exception.ErrorCode;
import com.highlight.highlight_backend.repository.user.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class UserSignUpService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

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

        /**
         * 휴대폰 인증 로직은 아직 미구현
         */
        user.setPhoneVerified(false);

        /**
         * 유저 repo 에 저장
         */
        user.setOver14(signUpRequestDto.isOver14());
        user.setAgreedToTerms(signUpRequestDto.isAgreedToTerms());
        user.setMarketingEnabled(signUpRequestDto.isMarketingEnabled());
        user.setEventSnsEnabled(signUpRequestDto.isEventSnsEnabled());

        userRepository.save(user);
        return signUpRequestDto;
    }
}
