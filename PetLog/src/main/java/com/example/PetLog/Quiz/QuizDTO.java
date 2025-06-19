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

    public static QuizDTO fromEntity(QuizEntity entity) {
        QuizDTO dto = new QuizDTO();
        dto.setQuizId(entity.getQuizId());
//        dto.setUserId(entity.getUserId());
        dto.setQuizQuestion(entity.getQuizQuestion());
        dto.setQuizOption1(entity.getQuizOption1());
        dto.setQuizOption2(entity.getQuizOption2());
        dto.setQuizOption3(entity.getQuizOption3());
        dto.setQuizOption4(entity.getQuizOption4());
        dto.setQuizAnswer(entity.getQuizAnswer());
        return dto;
    }

    public QuizEntity toEntity() {
        QuizEntity entity = new QuizEntity();
        entity.setQuizId(this.quizId);
//        entity.setUserId(this.userId);
        entity.setQuizQuestion(this.quizQuestion);
        entity.setQuizOption1(this.quizOption1);
        entity.setQuizOption2(this.quizOption2);
        entity.setQuizOption3(this.quizOption3);
        entity.setQuizOption4(this.quizOption4);
        entity.setQuizAnswer(this.quizAnswer);
        return entity;
    }

}
