package com.example.PetLog.ItemUser;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemUserRepository extends JpaRepository<ItemUserEntity,Long> {

    List<ItemUserEntity> findByUserId(Long userId);
}
