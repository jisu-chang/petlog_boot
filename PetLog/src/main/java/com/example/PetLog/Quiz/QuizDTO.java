package com.example.PetLog.Quiz;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class QuizDTO {
    Long quizId;
    String quizQuestion;
    String quizOption1;
    String quizOption2;
    String quizOption3;
    String quizOption4;
    String quizAnswer;

}
