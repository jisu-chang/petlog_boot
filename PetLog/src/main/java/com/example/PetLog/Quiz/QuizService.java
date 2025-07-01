package com.example.PetLog.Quiz;

import java.util.List;

public interface QuizService {

    void insertQuiz(QuizEntity quizEntity);

    List<QuizEntity> allout();

    QuizEntity updateByQuizId(Long quizId);

    void updateSave(QuizEntity entity);

    QuizEntity DeleteByQuizId(Long quizId);

    void deleteSave(Long quizId);

    QuizDTO getRandomUnsolvedQuiz(Long userId);

    QuizDTO getQuizById(Long quizId);

    List<QuizDTO> getQuizListByIds(List<Long> quizIds);

    boolean checkAnswer(Long quizId, String quizAnswer);
}
