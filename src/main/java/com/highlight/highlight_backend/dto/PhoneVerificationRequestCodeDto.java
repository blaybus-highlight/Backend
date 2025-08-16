package com.highlight.highlight_backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PhoneVerificationRequestCodeDto {

    @NotBlank(message = "휴대폰 번호는 필수입니다.")
    private String phoneNumber;
}
