package com.example.PetLog.QuizResult;

import com.example.PetLog.Quiz.QuizDTO;
import com.example.PetLog.Quiz.QuizService;
import com.example.PetLog.User.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
public class QuizResultController {

    @Autowired
    QuizResultService quizResultService;
    @Autowired
    QuizService quizService;
    @Autowired
    UserService userService;


    //퀴즈 결과 저장
    @PostMapping("/QuizSave")
    public String saveQuizResult(@ModelAttribute QuizResultDTO resultDTO,
                                 @RequestParam("quizAnswer") String quizAnswer,
                                 HttpSession session, Model mo) {
        Long userId = (Long) session.getAttribute("userId");
        String userLoginId = (String) session.getAttribute("userLoginId");
        if (userId == null) return "redirect:/login";

        boolean isCorrect = quizService.checkAnswer(resultDTO.getQuizId(), quizAnswer);

        resultDTO.setUserLoginId(userLoginId);
        resultDTO.setUserId(userId);
        resultDTO.setUserAnswer(quizAnswer); // DB 저장은 아님, DTO만

        // QuizResultDTO에 결과 점수 설정 (1: 정답, 0: 오답)
        resultDTO.setResultScore(isCorrect ? 1 : 0);

        try {
            quizResultService.saveResult(resultDTO, quizAnswer);
            if (isCorrect) {
                userService.addGrapes(userId, 3);
                userService.findUserById(userId).ifPresent(updatedUser -> {
                    session.setAttribute("grapeCount", updatedUser.getGrapeCount());
                    System.out.println("퀴즈 정답! 포도알 적립: " + updatedUser.getGrapeCount());
                });
            }
            // userAnswerMap을 안전하게 가져오고, 없으면 새로 만듦
            Map<Long, String> userAnswerMap = (Map<Long, String>) session.getAttribute("userAnswerMap");
            if (userAnswerMap == null) {
                userAnswerMap = new java.util.HashMap<>();
            }
            // map에 저장
            userAnswerMap.put(resultDTO.getQuizId(), quizAnswer);
            session.setAttribute("userAnswerMap", userAnswerMap);

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
        String userLoginId = (String) session.getAttribute("userLoginId");

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

        String userAnswer = (String) session.getAttribute("userAnswer"); //세션에서 꺼내기
        resultDTO.setUserAnswer(userAnswer); // 다시 DTO에 넣어줌
        resultDTO.setUserLoginId(userLoginId);

        // TOP 10 랭킹
        List<QuizResultDTO> top10 = quizResultService.getTop10ByQuizId(resultDTO.getUserId(), resultDTO.getQuizId());

        model.addAttribute("isCorrect", resultDTO.getResultScore() == 1);
        model.addAttribute("result", resultDTO); // DTO를 모델에 추가
        model.addAttribute("top10", top10);

        return "Quiz/UserQuizResult";
    }

    @GetMapping("/UserQuizOut")
    public String userQuizOut(HttpSession session, Model model) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return "redirect:/login";

        List<QuizResultDTO> resultList = quizResultService.getUserQuizList(userId);
        Map<Long, String> userAnswerMap = (Map<Long, String>) session.getAttribute("userAnswerMap");

        if (userAnswerMap != null) {
            for (QuizResultDTO dto : resultList) {
                Long qid = dto.getQuizId();
                String userAns = userAnswerMap.get(qid);
            }
        } else {
            System.out.println("userAnswerMap is NULL in session");
        }

        List<Long> quizIds = resultList.stream().map(QuizResultDTO::getQuizId).distinct().toList();
        List<QuizDTO> quizList = quizService.getQuizListByIds(quizIds);

        model.addAttribute("dtoList", resultList);
        model.addAttribute("quizList", quizList);
        model.addAttribute("optionFields", List.of("quizOption1", "quizOption2", "quizOption3", "quizOption4"));

        return "Quiz/userQuizOut";
    }
}
