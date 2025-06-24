package com.example.PetLog;

import com.example.PetLog.Comments.CommentsEntity;
import com.example.PetLog.Comments.CommentsService;
import com.example.PetLog.Likes.LikesService;
import com.example.PetLog.Pet.PetDTO;
import com.example.PetLog.Pet.PetService;
import com.example.PetLog.Quiz.QuizDTO;
import com.example.PetLog.Quiz.QuizService;
import com.example.PetLog.Snack.SnackEntity;
import com.example.PetLog.Snack.SnackService;
import com.example.PetLog.User.CustomUserDetails;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
public class HomeController {
    @Autowired
    PetService petService;

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

    @GetMapping("/")
    public String showMain(Model model, @AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails != null) {
            Long userId = userDetails.getUser().getUserId();
            List<PetDTO> pets = petService.findPetsByUserId(userId);
            System.out.println("🐶 등록된 펫 수: " + pets.size());
            model.addAttribute("list", pets);
        }
        return "main";
    }


}