package com.example.PetLog.QuizResult;

import java.util.List;

public interface QuizResultService {

//    void saveResult(QuizResultDTO resultDTO);

    QuizResultEntity getLatestResultByUser(Long userId);

    List<QuizResultDTO> getTop10ByQuizId(Long userId, Long quizId);

    QuizResultDTO getLatestResultDtoByUser(Long userId);

    void saveResult(QuizResultDTO dto, String quizAnswer);

    List<QuizResultDTO> getUserQuizList(Long userId);

    void deleteByUserId(Long userId);

}
