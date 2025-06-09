package com.example.PetLog.Likes;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LikesRepository extends JpaRepository<LikesEntity, Long> {

    List<LikesEntity> findAllByUser_UserId(Long userId);

    void deleteByUser_UserId(Long userId);
}
