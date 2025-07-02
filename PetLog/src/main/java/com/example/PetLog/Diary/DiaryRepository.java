package com.example.PetLog.Diary;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface DiaryRepository extends JpaRepository<DiaryEntity, Long> {

    List<DiaryEntity> findByUserId(Long userId);

    List<DiaryEntity> findAllByUser_UserId(Long userId);

    @Transactional
    void deleteByUser_UserId(Long userId);

    @Query("SELECT d FROM DiaryEntity d " +
            "WHERE TO_CHAR(d.diaryDate, 'YYYY') = :year " +
            "AND TO_CHAR(d.diaryDate, 'MM') = LPAD(:month, 2, '0') " +
            "AND d.userId = :userId " +
            "AND d.petId = :petId")
    List<DiaryEntity> findDiaryByMonth(
            @Param("userId") Long userId,
            @Param("year") String year,
            @Param("month") String month,
            @Param("petId") Long petId
    );

    // Oracle 11g 이하 네이티브 페이징 쿼리
    @Query(value =
            "SELECT * FROM ( " +
                    "  SELECT a.*, ROWNUM rnum FROM ( " +
                    "    SELECT * FROM diary WHERE user_id = :userId ORDER BY diary_date DESC " +
                    "  ) a WHERE ROWNUM <= :offset + :limit " +
                    ") WHERE rnum > :offset",
            nativeQuery = true)
    List<DiaryEntity> findDiaryByUserIdPagedNative(
            @Param("userId") Long userId,
            @Param("offset") int offset,
            @Param("limit") int limit);

    @Query(value = "SELECT COUNT(*) FROM diary WHERE user_id = :userId", nativeQuery = true)
    int countByUserIdNative(@Param("userId") Long userId);

    int countByUserId(Long userId);

    List<DiaryEntity> findByUser_UserId(Long userId);
}


