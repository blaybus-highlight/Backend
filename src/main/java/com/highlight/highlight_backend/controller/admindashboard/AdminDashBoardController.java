package com.highlight.highlight_backend.controller.admindashboard;


import com.highlight.highlight_backend.dto.ResponseDto;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Tag(name = "관리자 대쉬보드 설정", description = "관리자 대쉬보드 API")
public class AdminDashBoardController {

    @GetMapping("/stats")
    public ResponseEntity<ResponseDto<>> getStats() {


    }
}
