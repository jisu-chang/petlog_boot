package com.example.PetLog.QnA;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name = "qna")
@SequenceGenerator(
        name ="qna_id",
        sequenceName = "qna_seq",
        allocationSize = 1,
        initialValue = 1
)
public class QnAEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,generator = "qna_id")
    @Column (name = "qna_id")
    Long qnaId;
    @Column (name = "user_id")
    Long userId;
    @Column (name = "qna_title")
    String qnaTitle;
    @Column (name = "qna_content")
    String qnaContent;
    @Column (name = "qna_status")
    String qnaStatus;
    @Column (name = "qna_date")
    LocalDate qnaDate;
    @Column (name = "qna_answer")
    String qnaAnswer;
    @Column (name = "user_login_id")
    String userLoginId;
}
