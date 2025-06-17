package com.example.PetLog.Quiz;

import java.util.List;

public interface QuizService {

    void insertQuiz(QuizEntity quizEntity);

    List<QuizEntity> allout();
}
