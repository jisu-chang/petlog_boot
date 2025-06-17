package com.example.PetLog.QuizResult;

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

    //String userLoginId;
}
