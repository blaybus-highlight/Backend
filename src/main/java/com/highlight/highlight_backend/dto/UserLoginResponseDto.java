package com.highlight.highlight_backend.dto;

import com.highlight.highlight_backend.domain.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class UserLoginResponseDto {


    private String userId;

    private String nickname;

    private String accessToken;

    private String refreshToken;

}
