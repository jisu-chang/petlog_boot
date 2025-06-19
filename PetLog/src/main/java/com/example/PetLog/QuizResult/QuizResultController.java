package com.example.PetLog.QuizResult;

import com.example.PetLog.Quiz.QuizService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class QuizResultController {

    @Autowired
    QuizResultService quizResultService;
    @Autowired
    QuizService quizService;


    //퀴즈 결과 저장
    @PostMapping("/QuizSave")
    public String saveQuizResult(@ModelAttribute QuizResultDTO dto, @RequestParam("quizAnswer") int quizAnswer, HttpSession session, Model mo) {
        Long userId = (Long) session.getAttribute("userId");
        String userRole = (String) session.getAttribute("userRole");
        if (userId == null) {
            return "redirect:/login";
        }

        System.out.println("quizId = " + dto.getQuizId());
        dto.setUserId(userId);

        try {
            quizResultService.saveResult(dto, quizAnswer);
        } catch (IllegalStateException e) {
            mo.addAttribute("message", e.getMessage());
            return "main";
        }
        return "redirect:/QuizResultPage";
    }

    //퀴즈 결과 페이지
    @GetMapping("/QuizResultPage")
    public String QuizResultPage(HttpSession session, Model model) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login"; // 로그인 페이지로 리다이렉트
        }

        // DTO를 반환하는 서비스 메서드 호출
        QuizResultDTO resultDTO = quizResultService.getLatestResultDtoByUser(userId);

        if (resultDTO == null) {
            model.addAttribute("message", "풀이 결과가 없습니다.");
            // 결과가 없는 경우 처리할 뷰 이름 (예: 별도의 메시지 페이지 또는 현재 페이지 유지)
            return "Quiz/UserQuizResult"; // 또는 다른 오류 페이지
        }

        // 최신 결과의 정답 여부
        boolean isCorrect = resultDTO.getResultScore() == 1;

        // TOP 10 랭킹
        // top10을 가져올 때 quizId가 null이 되지 않도록 유의
        List<QuizResultDTO> top10 = quizResultService.getTop10ByQuizId(resultDTO.getUserId(), resultDTO.getQuizId());

//        model.addAttribute("isCorrect", isCorrect);
        model.addAttribute("isCorrect", resultDTO.getResultScore() == 1);
        model.addAttribute("result", resultDTO); // DTO를 모델에 추가
        model.addAttribute("top10", top10);

        return "Quiz/UserQuizResult";
    }
}
