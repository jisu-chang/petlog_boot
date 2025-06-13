package com.example.PetLog.PasswordEncoder;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordEncoderTest {

        public static void main(String[] args) {
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            String rawPassword = "admin1234"; // 원하는 비밀번호
            String encodedPassword = encoder.encode(rawPassword);
            System.out.println("암호화 된 비밀번호: " + encodedPassword);
        }
}