package com.example.PetLog.Likes;


import com.example.PetLog.Community.CommunityService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class LikesController {

    @Autowired
    LikesService likesService;

    @PostMapping("/post/{postId}/like")
    public String likeOnPost(@PathVariable Long postId, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        String userLoginId = (String) session.getAttribute("userLoginId");

        if (userId == null || userLoginId == null) {
            return "redirect:/login";
        }
        likesService.likeOnUser(postId, userId, userLoginId);

        return "redirect:/community/" + postId;  // 좋아요 후 상세 페이지로 리다이렉트
    }


}
