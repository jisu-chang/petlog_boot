package com.example.PetLog.Item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<ItemEntity,Long> {

    List<ItemEntity> findByItemStatus(String itemStatus);
}
