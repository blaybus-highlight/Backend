package com.highlight.highlight_backend.repository;

import com.highlight.highlight_backend.domain.PhoneVerification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PhoneVerificationRepository extends JpaRepository<PhoneVerification, String> {
}
