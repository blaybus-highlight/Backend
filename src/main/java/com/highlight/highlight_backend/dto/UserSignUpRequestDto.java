package com.highlight.highlight_backend.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

/**
 * 회원가입 시 User의 정보를 받을 DTO
 *
 * @Author 탁찬홍
 * @Since 2025.08.15
 */
@Getter
@Setter
public class UserSignUpRequestDto {

    @NotBlank(message = "아이디를 입력해주세요.")
    @Size(min = 4, max = 10, message = "아이디는 4자 이상 10자 이하로 입력해주세요.")
    private String userId;

    @NotBlank(message = "비밀번호를 입력해주세요.")
    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,15}$",
            message = "비밀번호는 영문, 숫자, 특수문자를 포함하여 8~15자로 입력해주세요.")
    private String password;

    @NotBlank(message = "닉네임을 입력해주세요.")
    @Size(min = 2, max = 8, message = "닉네임은 2자 이상 8자 이하로 입력해주세요.")
    private String nickname;

    @NotBlank(message = "휴대폰 번호를 입력해주세요.")
    @Pattern(regexp = "^010\\d{8}$", message = "휴대폰 번호 형식에 맞지 않습니다.")
    private String phoneNumber;

    /**
     * 인증 시 true
     */
    @AssertTrue(message = "휴대폰 인증이 필요 합니다.")
    private boolean isPhoneVerified;

    @NotNull(message = "만 14세 이상 동의 여부를 입력해주세요.")
    @AssertTrue(message = "만 14세 이상이어야 합니다.")
    private Boolean isOver14;

    @NotNull(message = "이용약관 동의 여부를 입력해주세요.")
    @AssertTrue(message = "이용약관에 동의해야 합니다.")
    private Boolean agreedToTerms;

    private boolean marketingEnabled;
    private boolean eventSnsEnabled;

}
