package com.example.PetLog.ItemUser;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemUserRepository extends JpaRepository<ItemUserEntity,Long> {

    // 전체 아이템
    List<ItemUserEntity> findByUserId(Long userId);

    // 특정 카테고리(예: 프레임) 아이템만
    List<ItemUserEntity> findByUserIdAndItem_ItemCategory(Long userId, String itemCategory);

    @Modifying
    @Transactional
    @Query("UPDATE ItemUserEntity i SET i.usertemEquip = 'N' " +
            "WHERE i.userId = :userId AND i.item.itemCategory = :category")
    void clearEquippedItems(@Param("userId") Long userId, @Param("category") String category);

    ItemUserEntity findByUserIdAndItem_ItemId(Long userId, Long itemId);

    List<ItemUserEntity> findByUserIdAndUsertemEquipAndItem_ItemCategory(Long userId, String y, String 프레임); //프로필 프레임 용

    int countByUserId(Long userId);

    List<ItemUserEntity> findByUser_UserId(Long userId);

    void deleteByUser_UserId(Long userId);
}