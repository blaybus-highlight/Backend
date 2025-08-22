package com.highlight.highlight_backend.dto.token;

import lombok.Setter;

@Setter
public class RefreshTokenResponseDto {

    private String adminId;

    private String adminName;

    private String accessToken;

    private String refreshToken;

    private String message;
}
