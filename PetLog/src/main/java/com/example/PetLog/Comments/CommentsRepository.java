package com.example.PetLog.Comments;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentsRepository extends JpaRepository<CommentsEntity, Long> {


    List<CommentsEntity> findAllByUser_UserId(Long userId);

    void deleteByUser_UserId(Long userId);
}
