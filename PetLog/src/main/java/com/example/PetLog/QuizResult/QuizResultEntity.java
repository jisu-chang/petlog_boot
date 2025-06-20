package com.example.PetLog.QuizResult;

import com.example.PetLog.Quiz.QuizEntity;
import com.example.PetLog.User.UserEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Table(name = "Quiz_Result1") //Quiz_Result1
@SequenceGenerator(
        name = "Quiz_result_id",
        sequenceName = "user_result_seq",
        allocationSize = 1,
        initialValue = 1
)
public class QuizResultEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,generator = "Quiz_result_id")
    @Column(name = "result_id")
    Long resultId;
    @Column(name = "result_score")
    int resultScore;
    @Column(name = "result_rank")
    int resultRank;
    @Column(name = "result_time")
    int resultTime;
    @Column(name = "user_id")
    Long userId;
    @Column(name = "quiz_id")
    Long quizId;
    @Column(name = "get_grape")
    int getGrape;
    @Column (name = "user_answer")
    String userAnswer;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", insertable = false, updatable = false)
    UserEntity user;  //userEntity 객체 설정하여 컬럼 추가 없이 유저 로그인 아이디도 가져다 쓸 수 있음

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "quiz_id", referencedColumnName = "quiz_id", insertable = false, updatable = false)
    QuizEntity quiz;

}
