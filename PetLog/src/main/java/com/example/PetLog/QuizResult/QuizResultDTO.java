package com.example.PetLog.QuizResult;

import com.example.PetLog.User.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class QuizResultDTO {
    Long resultId;
    int resultScore;
    int resultRank;
    int resultTime;
    Long userId;
    Long quizId;
    int getGrape;
    String userAnswer;
    UserEntity user;

    // QuizEntity 대신 필요한 필드들을 직접 DTO에 추가
    String quizAnswer;
    String quizOption1;
    String quizOption2;
    String quizOption3;
    String quizOption4;
    String userLoginId;

    public QuizResultDTO(QuizResultEntity entity) {
        this.resultId = entity.getResultId();
        this.getGrape = entity.getGetGrape();
        this.quizId = entity.getQuizId();
        this.resultRank = entity.getResultRank();
        this.resultScore = entity.getResultScore();
        this.resultTime = entity.getResultTime();
        this.userAnswer = entity.getUserAnswer();
        this.userId = entity.getUserId();
        this.user = entity.getUser();
    }
}