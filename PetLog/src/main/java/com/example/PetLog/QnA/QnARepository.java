package com.example.PetLog.QnA;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QnARepository extends JpaRepository<QnAEntity, Long> {

    List<QnAEntity> findByUserId(Long userId);

    List<QnAEntity> findByUser_UserId(Long userId);

    void deleteByUser_UserId(Long userId);

    // 전체 QnA 페이징
    @Query(value = "SELECT * FROM ( " +
            " SELECT t.*, ROWNUM rnum FROM ( " +
            "   SELECT * FROM qna ORDER BY qna_id DESC " +
            " ) t WHERE ROWNUM <= :endRow " +
            ") WHERE rnum >= :startRow",
            nativeQuery = true)
    List<QnAEntity> findPagedQnA(@Param("endRow") int endRow, @Param("startRow") int startRow);

    @Query(value = "SELECT COUNT(*) FROM qna", nativeQuery = true)
    int countAllQnA();

    // 내 QnA 페이징
    @Query(value = "SELECT * FROM ( " +
            " SELECT t.*, ROWNUM rnum FROM ( " +
            "   SELECT * FROM qna WHERE user_id = :userId ORDER BY qna_id DESC " +
            " ) t WHERE ROWNUM <= :endRow " +
            ") WHERE rnum >= :startRow",
            nativeQuery = true)
    List<QnAEntity> findPagedUserQnA(@Param("userId") Long userId,
                                     @Param("endRow") int endRow,
                                     @Param("startRow") int startRow);

    @Query(value = "SELECT COUNT(*) FROM qna WHERE user_id = :userId", nativeQuery = true)
    int countUserQnA(@Param("userId") Long userId);
}
