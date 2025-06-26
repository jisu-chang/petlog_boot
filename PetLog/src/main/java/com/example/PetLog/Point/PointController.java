package com.example.PetLog.Point;

import com.example.PetLog.User.UserEntity;
import com.example.PetLog.User.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class PointController {

    @Autowired
    UserRepository userRepository;
    @Autowired
    PointRepository pointRepository;

    // 🍇 사용자 포도 게이지 보기
    @GetMapping("/Point/GrapeBar")
    public String showGrapeGauge(HttpSession session, Model model) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }

        UserEntity user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return "redirect:/login";
        }

        int grapeCount = user.getGrapeCount();
        int maxGrape = 100;
        int percent = (int) ((double) grapeCount / maxGrape * 100);

        model.addAttribute("grapeCount", grapeCount);
        model.addAttribute("maxGrape", maxGrape);
        model.addAttribute("percent", percent);

        return "Point/GrapeBar";
    }

    @GetMapping("/Point/GrapeAdmin")
    public String showGrapeAdminPage(HttpSession session, Model model, RedirectAttributes redirectAttributes) {

        List<UserEntity> list = userRepository.findAll();
        model.addAttribute("list", list);
        return "Point/GrapeAdmin";
    }

    // ✅ 포도알 지급/차감 처리
    @PostMapping("/Point/GrapeAdmin")
    public String adjustGrapes(@RequestParam("user_id1") Long userId,
                               @RequestParam("grape_amount") int grapeAmount,
                               HttpSession session,
                               Model model) {


        UserEntity user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            model.addAttribute("message", "회원이 존재하지 않습니다.");
        } else {
            int current = user.getGrapeCount();
            int updated = current + grapeAmount;
            user.setGrapeCount(Math.max(updated, 0));
            userRepository.save(user);

            PointEntity point = PointEntity.builder()
                    .pointAction(grapeAmount > 0 ? "관리자 지급" : "관리자 차감")
                    .pointActionId(0L)
                    .pointEarnedGrapes((long) grapeAmount)
                    .user(user)
                    .build();
            pointRepository.save(point);

            model.addAttribute("message", "포도알이 성공적으로 반영되었습니다.");
        }

        List<UserEntity> list = userRepository.findAll();
        model.addAttribute("list", list);
        return "Point/GrapeAdmin";
    }

    @GetMapping("/Point/GrapeRank")
    public String showGrapeRanking(HttpSession session, Model model) {

        List<UserEntity> ranked = userRepository.findAllByOrderByGrapeCountDesc();
        model.addAttribute("list", ranked);
        return "Point/GrapeRank";
    }
}