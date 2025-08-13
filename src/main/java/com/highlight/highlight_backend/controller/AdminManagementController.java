package com.highlight.highlight_backend.controller;

import com.highlight.highlight_backend.dto.AdminCreateRequestDto;
import com.highlight.highlight_backend.dto.AdminResponseDto;
import com.highlight.highlight_backend.dto.AdminUpdateRequestDto;
import com.highlight.highlight_backend.dto.ResponseDto;
import com.highlight.highlight_backend.service.AdminManagementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * 관리자 계정 관리 컨트롤러
 * 
 * SUPER_ADMIN 권한을 가진 관리자가 다른 관리자 계정을 생성/수정/삭제할 수 있는 API를 제공합니다.
 * 
 * @author 전우선
 * @since 2025.08.13
 */
@Slf4j
@RestController
@RequestMapping("/api/admin-management")
@RequiredArgsConstructor
@Tag(name = "관리자 계정 관리 API", description = "백오피스 관리자 계정 생성/수정/삭제 관련 API")
public class AdminManagementController {
    
    private final AdminManagementService adminManagementService;
    
    /**
     * 관리자 계정 생성
     * 
     * @param request 계정 생성 요청 데이터
     * @param authentication 현재 로그인한 관리자 정보
     * @return 생성된 관리자 정보
     */
    @PostMapping("/admins")
    @Operation(summary = "관리자 계정 생성", description = "새로운 관리자 계정을 생성합니다. (SUPER_ADMIN 권한 필요)")
    public ResponseEntity<ResponseDto<AdminResponseDto>> createAdmin(
            @Valid @RequestBody AdminCreateRequestDto request,
            Authentication authentication) {
        
        Long currentAdminId = (Long) authentication.getPrincipal();
        log.info("POST /api/admin-management/admins - 관리자 계정 생성 요청: {} (요청자: {})", 
                request.getAdminId(), currentAdminId);
        
        AdminResponseDto response = adminManagementService.createAdmin(request, currentAdminId);
        
        return ResponseEntity.ok(
            ResponseDto.success(response, "관리자 계정이 성공적으로 생성되었습니다.")
        );
    }
    
    /**
     * 관리자 목록 조회
     * 
     * @param pageable 페이징 정보
     * @param authentication 현재 로그인한 관리자 정보
     * @return 관리자 목록
     */
    @GetMapping("/admins")
    @Operation(summary = "관리자 목록 조회", description = "관리자 목록을 페이징하여 조회합니다. (SUPER_ADMIN 권한 필요)")
    public ResponseEntity<ResponseDto<Page<AdminResponseDto>>> getAdminList(
            @PageableDefault(size = 20) Pageable pageable,
            Authentication authentication) {
        
        Long currentAdminId = (Long) authentication.getPrincipal();
        log.info("GET /api/admin-management/admins - 관리자 목록 조회 요청 (요청자: {})", currentAdminId);
        
        Page<AdminResponseDto> response = adminManagementService.getAdminList(pageable, currentAdminId);
        
        return ResponseEntity.ok(
            ResponseDto.success(response, "관리자 목록을 성공적으로 조회했습니다.")
        );
    }
    
    /**
     * 관리자 상세 조회
     * 
     * @param adminId 조회할 관리자 ID
     * @param authentication 현재 로그인한 관리자 정보
     * @return 관리자 상세 정보
     */
    @GetMapping("/admins/{adminId}")
    @Operation(summary = "관리자 상세 조회", description = "특정 관리자의 상세 정보를 조회합니다. (SUPER_ADMIN 권한 필요)")
    public ResponseEntity<ResponseDto<AdminResponseDto>> getAdmin(
            @PathVariable Long adminId,
            Authentication authentication) {
        
        Long currentAdminId = (Long) authentication.getPrincipal();
        log.info("GET /api/admin-management/admins/{} - 관리자 상세 조회 요청 (요청자: {})", 
                adminId, currentAdminId);
        
        AdminResponseDto response = adminManagementService.getAdmin(adminId, currentAdminId);
        
        return ResponseEntity.ok(
            ResponseDto.success(response, "관리자 정보를 성공적으로 조회했습니다.")
        );
    }
    
    /**
     * 관리자 계정 수정
     * 
     * @param adminId 수정할 관리자 ID
     * @param request 수정 요청 데이터
     * @param authentication 현재 로그인한 관리자 정보
     * @return 수정된 관리자 정보
     */
    @PutMapping("/admins/{adminId}")
    @Operation(summary = "관리자 계정 수정", description = "관리자 계정 정보 및 권한을 수정합니다. (SUPER_ADMIN 권한 필요)")
    public ResponseEntity<ResponseDto<AdminResponseDto>> updateAdmin(
            @PathVariable Long adminId,
            @Valid @RequestBody AdminUpdateRequestDto request,
            Authentication authentication) {
        
        Long currentAdminId = (Long) authentication.getPrincipal();
        log.info("PUT /api/admin-management/admins/{} - 관리자 계정 수정 요청 (요청자: {})", 
                adminId, currentAdminId);
        
        AdminResponseDto response = adminManagementService.updateAdmin(adminId, request, currentAdminId);
        
        return ResponseEntity.ok(
            ResponseDto.success(response, "관리자 계정이 성공적으로 수정되었습니다.")
        );
    }
    
    /**
     * 관리자 계정 삭제
     * 
     * @param adminId 삭제할 관리자 ID
     * @param authentication 현재 로그인한 관리자 정보
     * @return 삭제 완료 메시지
     */
    @DeleteMapping("/admins/{adminId}")
    @Operation(summary = "관리자 계정 삭제", description = "관리자 계정을 삭제합니다. (SUPER_ADMIN 권한 필요)")
    public ResponseEntity<ResponseDto<String>> deleteAdmin(
            @PathVariable Long adminId,
            Authentication authentication) {
        
        Long currentAdminId = (Long) authentication.getPrincipal();
        log.info("DELETE /api/admin-management/admins/{} - 관리자 계정 삭제 요청 (요청자: {})", 
                adminId, currentAdminId);
        
        adminManagementService.deleteAdmin(adminId, currentAdminId);
        
        return ResponseEntity.ok(
            ResponseDto.success("SUCCESS", "관리자 계정이 성공적으로 삭제되었습니다.")
        );
    }
}