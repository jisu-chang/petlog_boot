package com.example.PetLog.Snack;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SnackRepository extends JpaRepository<SnackEntity,Long> {
    List<SnackEntity> findAllByUser_UserId(Long userId);

    void deleteByUser_UserId(Long userId);
}
