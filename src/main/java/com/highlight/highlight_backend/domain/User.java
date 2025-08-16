package com.highlight.highlight_backend.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "user")
@Getter
@Setter
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@SQLDelete(sql = "UPDATE user SET deleted_at = NOW() WHERE id = ?")
@Where(clause = "deleted_at IS NULL")
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    /**
     * 유저 입력
     */
    @Column(nullable = false, unique = true)
    private String userId;  // ID

    @Column(nullable = false)
    private String password;  // PW

    @Column(nullable = false, unique = true)
    private String nickname;

    @Column(nullable = false, unique = true)
    private String phoneNumber;
    
    /**
     * 휴대폰 인증 여부
     */
    @Column(nullable = false)
    private boolean isPhoneVerified = false; // 기본값은 false로 설정

    private String verificationCode; // 휴대폰 인증 코드
    private LocalDateTime verificationCodeExpiresAt; // 인증 코드 만료 시간

    /**
     * 결제수단 등록
     * 
     * 이 부분은 정해지면 추후에 추가 예정
     */


    /**
     * 약관 동의
     */
    @Column(nullable = false)
    private boolean isOver14;  // 14세 이상 여부

    @Column(nullable = false)
    private boolean agreedToTerms;  // 이용약관 동의 여부

    @Column(nullable = false)
    private boolean marketingEnabled;  // 개인정보 마케팅 활용 여부

    @Column(nullable = false)
    private boolean eventSnsEnabled;  // 이벤트 광고 SNS 수신 여부

    private LocalDateTime deletedAt;
}