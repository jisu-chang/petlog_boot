package com.example.PetLog.QuizResult;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name = "Quiz_Result")
@SequenceGenerator(
        name = "Quiz_result_id",
        sequenceName = "Quiz_result_seq",
        allocationSize = 1,
        initialValue = 1
)
public class QuizResultEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,generator = "Quiz_result_id")
    @Column
    Long resultId;
    @Column
    int resultScore;
    @Column
    int resultRank;
    @Column
    int resultTime;
    @Column
    Long userId;
    @Column
    Long quizId;
    @Column
    int getGrape;
}
