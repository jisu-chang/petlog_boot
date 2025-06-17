package com.example.PetLog.Quiz;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class QuizController {

    @Autowired
    QuizService quizService;

    @GetMapping("/QuizInput")
    public String quizin(){
        return "Quiz/AdminQuizInput";
    }

    @PostMapping("/QuizInputSave")
    public String quizsave(){

        return "redirect:/";
    }
}
