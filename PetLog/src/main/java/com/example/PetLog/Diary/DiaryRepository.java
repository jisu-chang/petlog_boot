package com.example.PetLog.Diary;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface DiaryRepository extends JpaRepository<DiaryEntity,Long> {

    List<DiaryEntity> findByUserId(Long userId);

    //지수 추가 - 회원탈퇴
    List<DiaryEntity> findAllByUser_UserId(Long userId);

    //지수 추가 - 회원탈퇴
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

    int countByUserId(Long userId);
}
