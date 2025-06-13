package com.example.PetLog;

import com.example.PetLog.User.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping(value = "/")
    public String home() {
        return "main";
    }

    @GetMapping(value = "/main")
    public String home1(Model mo, @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        if (customUserDetails != null) {
            boolean isAdmin = customUserDetails.getAuthorities().stream()
                    .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
            mo.addAttribute("user_role", isAdmin ? "admin" : "USER");
        }
        return "main";
    }

    @GetMapping("/test")
    public String testSecurity(@AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails != null) {
            System.out.println("현재 로그인한 사용자 역할: " + userDetails.getUserRole());
        } else {
            System.out.println("사용자가 로그인되지 않음.");
        }
        return "test";
    }

}