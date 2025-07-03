package com.example.PetLog.Point;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PointRepository extends JpaRepository<PointEntity,Long> {
    
    List<PointEntity> findByUser_UserId(Long userId);

    void deleteByUser_UserId(Long userId);
}
