package com.example.PetLog.Quiz;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QuizServiceImp implements QuizService{

    @Autowired
    QuizRepository quizRepository;


    @Override
    public void insertQuiz(QuizEntity quizEntity) {
        quizRepository.save(quizEntity);
    }

    @Override
    public List<QuizEntity> allout() {
        return quizRepository.findAll();
    }
}
