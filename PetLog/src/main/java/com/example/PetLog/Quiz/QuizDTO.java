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


    public QuizEntity toEntity() {
        QuizEntity entity = new QuizEntity();
        entity.setQuizId(this.quizId);
        entity.setQuizQuestion(this.quizQuestion);
        entity.setQuizAnswer(this.quizAnswer); //this가 자기 필드
        entity.setQuizOption1(this.quizOption1);
        entity.setQuizOption2(this.quizOption2);
        entity.setQuizOption3(this.quizOption3);
        entity.setQuizOption4(this.quizOption4);
        return entity;
    }
}
