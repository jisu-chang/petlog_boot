package com.example.PetLog.Diary;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Pageable;

import java.util.List;

@Repository
public interface DiaryRepository extends JpaRepository<DiaryEntity,Long> {

    List<DiaryEntity> findByUserId(Long userId);
}
