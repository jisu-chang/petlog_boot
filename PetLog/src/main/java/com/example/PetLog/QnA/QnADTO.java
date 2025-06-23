package com.example.PetLog.QnA;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;


@AllArgsConstructor
@NoArgsConstructor
@Data
public class QnADTO {
    Long qnaId;
    Long userId;
    String qnaTitle;
    String qnaContent;
    String qnaStatus;
    LocalDate qnaDate;
    String qnaAnswer;
    String userLoginId;

    public QnAEntity entity() {
        QnAEntity entity = new QnAEntity();
        entity.setQnaId(this.qnaId);
        entity.setUserId(this.userId);
        entity.setQnaTitle(this.qnaTitle);
        entity.setQnaContent(this.qnaContent);
        entity.setQnaStatus(this.qnaStatus);
        entity.setQnaDate(this.qnaDate);
        entity.setQnaAnswer(this.qnaAnswer);
        return entity;
    }
}
