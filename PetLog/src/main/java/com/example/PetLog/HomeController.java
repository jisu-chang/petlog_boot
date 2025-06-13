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
            mo.addAttribute("user_role", customUserDetails.getAuthorities().toString().contains("ROLE_ADMIN") ? "admin" : "USER");
        }
        return "main";
    }
}