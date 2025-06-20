package com.example.PetLog.QnA;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Date;

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

}
