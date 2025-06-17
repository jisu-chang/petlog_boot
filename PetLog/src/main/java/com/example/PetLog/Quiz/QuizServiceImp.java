package com.example.PetLog.Quiz;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class QuizServiceImp implements QuizService{

    @Autowired
    QuizRepository quizRepository;


}
