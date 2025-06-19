package com.example.PetLog.Quiz;

import com.example.PetLog.User.UserEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name = "Quiz")
@SequenceGenerator(
        name ="Quiz_id",
        sequenceName = "Quiznum_seq",
        allocationSize = 1,
        initialValue = 1
)
public class QuizEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,generator = "Quiz_id")
    @Column (name = "quiz_id")
    Long quizId;
    @Column (name = "quiz_question")
    String quizQuestion;
    @Column (name = "quiz_option1")
    String quizOption1;
    @Column (name = "quiz_option2")
    String quizOption2;
    @Column (name = "quiz_option3")
    String quizOption3;
    @Column (name = "quiz_option4")
    String quizOption4;
    @Column (name = "quiz_answer")
    String quizAnswer;

//    @ManyToOne(fetch = FetchType.EAGER)
//    @JoinColumn(name = "user_id", referencedColumnName = "user_id", insertable = false, updatable = false)
//    UserEntity user;  //userEntity 객체 설정하여 컬럼 추가 없이 유저 로그인 아이디도 가져다 쓸 수 있음
}
