package com.example.PetLog.Pet;

import com.example.PetLog.User.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PetRepository extends JpaRepository<PetEntity,Long> {

    List<PetEntity> findByUser(UserEntity user);

    List<PetEntity> findByUserUserId(Long userId);

    List<PetEntity> findByUser_UserId(Long userId);

    void deleteByUser_UserId(Long userId);
}
