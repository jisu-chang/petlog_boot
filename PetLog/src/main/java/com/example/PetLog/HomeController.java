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
    public String home1(Model model, @AuthenticationPrincipal CustomUserDetails customUserDetails, HttpSession session) {

        Long userId = null;
        String userRole = null;

        // 일반 로그인 시
        if (customUserDetails != null) {
            userId = customUserDetails.getUser().getUserId();
            userRole = customUserDetails.getUser().getUserRole();
        } else {
            // 카카오 로그인 시
            userId = (Long) session.getAttribute("userId");
            userRole = (String) session.getAttribute("userRole");
        }

        List<PetDTO> pets = new ArrayList<>();
        UserEntity user = null;

        if (userId != null) {
            pets = petService.findPetsByUserId(userId);
            user = userService.findByuserId(userId);
            model.addAttribute("user_role", userRole);
        }

        model.addAttribute("list", pets);
        model.addAttribute("user", user);
        return "main";
    }

    @GetMapping("/")
    public String showMain(Model model, @AuthenticationPrincipal CustomUserDetails customUserDetails, HttpSession session) {
        Long userId = null;
        String userRole = null;

        // 일반 로그인 시
        if (customUserDetails != null) {
            userId = customUserDetails.getUser().getUserId();
            userRole = customUserDetails.getUser().getUserRole();
        } else {
            // 카카오 로그인 시
            userId = (Long) session.getAttribute("userId");
            userRole = (String) session.getAttribute("userRole");
        }

        List<PetDTO> pets = new ArrayList<>();
        UserEntity user = null;

        if (userId != null) {
            pets = petService.findPetsByUserId(userId);
            user = userService.findByuserId(userId);
            model.addAttribute("user_role", userRole);
        }

        model.addAttribute("list", pets);
        model.addAttribute("user", user);
        return "main";
    }


}