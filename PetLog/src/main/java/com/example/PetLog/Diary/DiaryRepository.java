package com.example.PetLog.Diary;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DiaryRepository extends JpaRepository<DiaryEntity,Long> {

    List<DiaryEntity> findByUserId(Long userId);

    //지수 추가 - 회원탈퇴
    List<DiaryEntity> findAllByUser_UserId(Long userId);

    //지수 추가 - 회원탈퇴
    void deleteByUser_UserId(Long userId);
}
