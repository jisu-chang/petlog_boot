package com.example.PetLog.ItemUser;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemUserRepository extends JpaRepository<ItemUserEntity,Long> {
}
