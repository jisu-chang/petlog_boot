package com.example.PetLog.Point;

import com.example.PetLog.User.UserDTO;
import com.example.PetLog.User.UserEntity;
import com.example.PetLog.User.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.SessionAttribute;

@Controller
public class PointController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/Point/GrapeBar")
    public String showGrapeGauge(HttpSession session, Model model) {
        // 세션에서 userId 꺼냄
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }

        // 사용자 정보 조회
        UserEntity user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return "redirect:/login";
        }

        // 현재 포도 수
        int grapeCount = user.getGrapeCount();
        int maxGrape = 100;

        // 퍼센트 계산
        int percent = (int) ((double) grapeCount / maxGrape * 100);

        // 모델에 담아서 뷰로 전달
        model.addAttribute("grapeCount", grapeCount);
        model.addAttribute("maxGrape", maxGrape);
        model.addAttribute("percent", percent);

        return "Point/GrapeBar";
    }
}
