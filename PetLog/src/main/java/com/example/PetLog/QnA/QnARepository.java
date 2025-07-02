package com.example.PetLog.QnA;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QnARepository extends JpaRepository<QnAEntity, Long> {

    List<QnAEntity> findByUserId(Long userId);

    List<QnAEntity> findByUser_UserId(Long userId);

    void deleteByUser_UserId(Long userId);
}
