package com.highlight.highlight_backend.controller.user;


import com.highlight.highlight_backend.dto.ResponseDto;
import com.highlight.highlight_backend.dto.token.RefreshTokenResponseDto;
import com.highlight.highlight_backend.util.JwtUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 마이페이지 컨트롤러
 *
 * @author 탁찬홍
 * @since 2025.08.23
 */
@Slf4j
@RestController
@RequestMapping("/api/public/refresh")
@RequiredArgsConstructor
@Tag(name = "마이페이지", description = "RefreshToken 으로 갱신 API")
public class RefreshTokenController {

    private final JwtUtil jwtUtil;

    @PostMapping
    public ResponseEntity<ResponseDto<RefreshTokenResponseDto>> getRefreshToken(
            @RequestBody String refreshToken) {

        Boolean isValid = jwtUtil.validateToken(refreshToken);

        if (isValid) {
            RefreshTokenResponseDto refreshTokenResponseDto = new RefreshTokenResponseDto();
            Long id = jwtUtil.getUserId(refreshToken);
            String email = jwtUtil.getEmail(refreshToken);
            String role = jwtUtil.getRole(refreshToken);
            String getAccessToken =  jwtUtil.generateAccessToken(id,email,role);
            refreshTokenResponseDto.setRefreshToken(getAccessToken);

            return ResponseEntity.ok(ResponseDto.success(refreshTokenResponseDto, "토큰 생성 성공!"));
        }
        return ResponseEntity.notFound().build();
    }
}
