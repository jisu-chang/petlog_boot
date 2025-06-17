package com.example.PetLog.Diary;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DiaryRepository extends JpaRepository<DiaryEntity,Long> {

    List<DiaryEntity> findByUserId(Long userId);

    //지수 추가 - 회원탈퇴
    List<DiaryEntity> findAllByUser_UserId(Long userId);

    //지수 추가 - 회원탈퇴
    void deleteByUser_UserId(Long userId);

    @Query("SELECT d FROM DiaryEntity d " +
            "WHERE FUNCTION('YEAR', d.diaryDate) = :year " +
            "AND FUNCTION('MONTH', d.diaryDate) = :month " +
            "AND d.userId = :userId " +
            "AND d.petId = :petId")
    List<DiaryEntity> findDiaryByMonth(
            @Param("userId") Long userId,
            @Param("year") int year,
            @Param("month") int month,
            @Param("petId") Long petId
    );
}
