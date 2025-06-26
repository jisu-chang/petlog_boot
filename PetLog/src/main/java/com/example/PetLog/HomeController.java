package com.example.PetLog;

import com.example.PetLog.Comments.CommentsEntity;
import com.example.PetLog.Comments.CommentsService;
import com.example.PetLog.Likes.LikesService;
import com.example.PetLog.Pet.PetDTO;
import com.example.PetLog.Pet.PetService;
import com.example.PetLog.Quiz.QuizDTO;
import com.example.PetLog.Quiz.QuizService;
import com.example.PetLog.Snack.SnackEntity;
import com.example.PetLog.Community.CommunityDTO;
import com.example.PetLog.Community.CommunityService;
import com.example.PetLog.Pet.PetDTO;
import com.example.PetLog.Pet.PetService;
import com.example.PetLog.Snack.SnackDTO;
import com.example.PetLog.Snack.SnackService;
import com.example.PetLog.User.CustomUserDetails;
import com.example.PetLog.User.UserDTO;
import com.example.PetLog.User.UserEntity;
import com.example.PetLog.User.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.ArrayList;
import java.util.List;

@Controller
public class HomeController {
    @Autowired
    PetService petService;
    @Autowired
    UserService userService;

    @GetMapping(value = "/main")
    public String home1(Model model, @AuthenticationPrincipal CustomUserDetails userDetails, @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        if (customUserDetails != null) {
            boolean isAdmin = customUserDetails.getAuthorities().stream()
                    .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
            model.addAttribute("user_role", isAdmin ? "admin" : "USER");
        }
        List<PetDTO> pets = new ArrayList<>();
        UserEntity user = null;

        if (userDetails != null) {
            Long userId = userDetails.getUser().getUserId();
            pets = petService.findPetsByUserId(userId);
            user = userService.findByuserId(userId);
        }

        model.addAttribute("list", pets); // 무조건 list는 들어감 (빈 리스트라도)
        model.addAttribute("user", user);

        return "main4";
    }

    @GetMapping("/")
    public String showMain(Model model, @AuthenticationPrincipal CustomUserDetails userDetails, @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        if (customUserDetails != null) {
            boolean isAdmin = customUserDetails.getAuthorities().stream()
                    .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
            model.addAttribute("user_role", isAdmin ? "admin" : "USER");
        }

        List<PetDTO> pets = new ArrayList<>();
        UserEntity user = null;

        if (userDetails != null) {
            Long userId = userDetails.getUser().getUserId();
            pets = petService.findPetsByUserId(userId);
            user = userService.findByuserId(userId);
        }

        model.addAttribute("list", pets); // 무조건 list는 들어감 (빈 리스트라도)
        model.addAttribute("user", user);
        return "main4";
    }


}