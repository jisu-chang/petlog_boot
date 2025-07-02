package com.example.PetLog.Quiz;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuizRepository extends JpaRepository<QuizEntity, Long> {

    //풀지않은 퀴즈 찾기
    List<QuizEntity> findByQuizIdNotIn(List<Long> solvedQuizIds);

    //유저 퀴즈 목록 출력
    List<QuizEntity> findByQuizIdIn(List<Long> ids);

}
