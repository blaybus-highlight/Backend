package com.highlight.highlight_backend.repository.user;

import com.highlight.highlight_backend.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByUserId(String userId);
    boolean existsByNickname(String nickname);
    boolean existsByPhoneNumber(String phoneNumber);
}
