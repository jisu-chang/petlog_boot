package com.example.PetLog.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final HttpSession session;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication)
            throws IOException, ServletException {

        Boolean isSocialSignup = (Boolean) session.getAttribute("social_signup");

        if (Boolean.TRUE.equals(isSocialSignup)) {
            response.sendRedirect("/signUpKakao");  // 소셜 회원가입 화면으로 리다이렉트
        } else {
            response.sendRedirect("/main");  // 기존 회원이면 메인으로 이동
        }
    }
}