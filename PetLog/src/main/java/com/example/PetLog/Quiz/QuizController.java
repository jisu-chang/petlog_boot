package com.example.PetLog.Quiz;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
public class QuizController {

    @Autowired
    QuizService quizService;

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
}
