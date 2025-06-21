package com.example.PetLog.QnA;

import com.example.PetLog.User.UserEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
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
    @Column (name = "user_id", nullable = false)
    Long userId;
    @Column (name = "qna_title")
    String qnaTitle;
    @Lob
    @Column (name = "qna_content",  columnDefinition = "CLOB")
    String qnaContent;
    @Column (name = "qna_status")
    String qnaStatus;
    @Column (name = "qna_date")
    LocalDate qnaDate;
    @Lob
    @Column (name = "qna_answer",  columnDefinition = "CLOB")
    String qnaAnswer;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", insertable = false, updatable = false)
    UserEntity user;  //userEntity 객체 설정하여 컬럼 추가 없이 유저 로그인 아이디도 가져다 쓸 수 있음

}
