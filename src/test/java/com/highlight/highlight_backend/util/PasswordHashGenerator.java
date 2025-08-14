package com.highlight.highlight_backend.util;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootTest
public class PasswordHashGenerator {

    @Test
    public void generatePasswordHashes() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);
        
        String adminPassword = "Admin123!@#";
        String managerPassword = "Manager456!@#";
        
        String adminHash = encoder.encode(adminPassword);
        String managerHash = encoder.encode(managerPassword);
        
        System.out.println("=== BCrypt 해시 생성 결과 ===");
        System.out.println("Admin 비밀번호: " + adminPassword);
        System.out.println("Admin 해시: " + adminHash);
        System.out.println();
        System.out.println("Manager 비밀번호: " + managerPassword);
        System.out.println("Manager 해시: " + managerHash);
        System.out.println();
        System.out.println("=== UPDATE SQL ===");
        System.out.println("UPDATE admin SET password = '" + adminHash + "' WHERE admin_id = 'admin';");
        System.out.println("UPDATE admin SET password = '" + managerHash + "' WHERE admin_id = 'manager';");
    }
}