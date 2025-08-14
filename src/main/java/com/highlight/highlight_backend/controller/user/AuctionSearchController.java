package com.highlight.highlight_backend.controller.user;

import com.highlight.highlight_backend.dto.ResponseDto;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * +
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user/products")
@Tag(name = "일반 유저 경매 조회 API", description = "일반 유저가 확인하는 API 입니다.")
public class AuctionSearchController {

    @GetMapping("/")
    public ResponseEntity<ResponseDto<>> home () {

    }
}
