package com.example.PetLog.QuizResult;

import com.example.PetLog.Quiz.QuizRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class QuizResultServiceImp implements QuizResultService{

    @Autowired
    QuizRepository quizRepository;


}
