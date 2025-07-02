package com.example.PetLog.Quiz;

import com.example.PetLog.QuizResult.QuizResultService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class QuizController {

    @Autowired
    QuizService quizService;
    @Autowired
    QuizResultService quizResultRepository;

    @GetMapping("/QuizInput")
    public String quizin(){
        return "Quiz/AdminQuizInput";
    }

    @PostMapping("/QuizInputSave")
    public String quizsave(@ModelAttribute QuizDTO quizDTO, HttpSession session){
        Long userId = (Long) session.getAttribute("userId");

        QuizEntity quizEntity = quizDTO.toEntity();
        quizService.insertQuiz(quizEntity);
        return "redirect:/QuizOut";
    }

    @GetMapping("/QuizOut")
    public String quizOut(Model mo, HttpSession session){
        String userRole = (String) session.getAttribute("userRole");

        List<QuizEntity> dto = quizService.allout();
        mo.addAttribute("dto", dto);

        //옵션 필드 리스트 추가
        List<String> optionFields = List.of("quizOption1", "quizOption2", "quizOption3", "quizOption4");
        mo.addAttribute("optionFields", optionFields);
        return "Quiz/AdminQuizOut";
    }

    @GetMapping("/QuizUpdate")
    public String quizUpdate(@RequestParam("quizId") Long quizId,Model mo){
        QuizEntity qdto = quizService.updateByQuizId(quizId);
        mo.addAttribute("qdto", qdto);
        return "Quiz/AdminQuizUpdate";
    }

    @PostMapping("/QuizUpdateSave")
    public String quizUpdatesave(@RequestParam("quizId") Long quizId, QuizDTO quizDTO ,Model mo){
        QuizEntity entity = quizDTO.toEntity();
        quizService.updateSave(entity);
        return "redirect:/QuizOut";
    }

    @GetMapping("/QuizDelete")
    public String quizDelete(@RequestParam("quizId") Long quizId,Model mo){
        QuizEntity qdto = quizService.DeleteByQuizId(quizId);
        mo.addAttribute("qdto", qdto);
        return "Quiz/AdminQuizDelete";
    }

    @PostMapping("/QuizDeleteSave")
    public String quizDeletesave(@RequestParam("quizId") Long quizId, QuizDTO quizDTO ,Model mo){
        QuizEntity entity = quizDTO.toEntity();
        quizService.deleteSave(quizId);
        return "redirect:/QuizOut";
    }

    //풀지않은 퀴즈 랜덤 가져오기 - 유저 아이디 기준
    @GetMapping("/UserQuiz")
    public String userQuiz(Model mo, HttpSession session){
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }
        //유저 아이디로 해당 유저가 풀지않은 퀴즈 가져오기
        QuizDTO quiz = quizService.getRandomUnsolvedQuiz(userId);

        if(quiz==null){
            mo.addAttribute("allDone",true);
        } else {
            mo.addAttribute("dto", List.of(quiz));
        }
        return "Quiz/UserQuizPlay";
    }
}
