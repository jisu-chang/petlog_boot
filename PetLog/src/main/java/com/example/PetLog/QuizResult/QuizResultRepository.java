package com.example.PetLog.QuizResult;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface QuizResultRepository extends JpaRepository<QuizResultEntity, Long> {

    @Query(value = "SELECT * FROM (SELECT * FROM quiz_result1 WHERE user_id = :userId ORDER BY result_id DESC) WHERE ROWNUM = 1", nativeQuery = true)
    QuizResultEntity findTopByUserIdOrderByResultIdDesc(@Param("userId") Long userId);

    //풀지 않은 랜덤 퀴즈만 가져오기
    @Query("SELECT qr.quiz.quizId FROM QuizResultEntity qr WHERE qr.user.userId = :userId")
    List<Long> findSolvedQuizIdByUserIds(@Param("userId") Long userId);

    // 풀이시간 빠른 순 Top10
    @Query(value = """
    SELECT * FROM (SELECT qr.result_id, qr.result_score, qr.result_rank, qr.result_time, qr.user_id, qr.quiz_id, qr.get_grape, qr.user_answer, u.user_login_id FROM quiz_result1 qr
    JOIN user1 u ON qr.user_id = u.user_id WHERE qr.quiz_id = :quizId AND qr.result_score = 1 ORDER BY qr.result_score DESC, qr.result_time ASC) WHERE ROWNUM <= 10""", nativeQuery = true)
    List<Object[]> findTop10RawDataByQuizId(@Param("quizId") Long quizId);

    // 중복 저장 방지
    @Query(value = "SELECT count(*) FROM quiz_result1 WHERE user_id = :userId AND quiz_id = :quizId", nativeQuery = true)
    Integer countByUserIdAndQuizId(@Param("userId") Long userId, @Param("quizId") Long quizId);

    @Query("select count(qr) from QuizResultEntity qr where qr.quizId=:quizId and qr.resultScore =1 and qr.resultTime<:resultTime")
    int countRankByQuizIdAndFasterTime(@Param("quizId") Long quizId, @Param("resultTime") int resultTime);

    List<QuizResultEntity> findByUserId(Long userId);

    int countByUserIdAndResultScore(Long userId, int i);

    List<QuizResultEntity> findByUser_UserId(Long userId);

    void deleteByUserId(Long userId);

    void deleteByQuizId(Long quizId);
}

