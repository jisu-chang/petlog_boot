package com.example.PetLog.ItemUser;

import com.example.PetLog.Item.ItemDTO;
import com.example.PetLog.Item.ItemEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ItemUserServiceImp implements ItemUserService {

    @Autowired
    ItemUserRepository itemUserRepository;

    @Override
    public List<ItemDTO> findFrameItemsByUserId(Long userId) {
        List<ItemUserEntity> itemUsers = itemUserRepository.findByUserIdAndItem_ItemCategory(userId, "프레임");

        return itemUsers.stream().map(entity -> {
            ItemEntity item = entity.getItem();
            return ItemDTO.builder()
                    .itemId(item.getItemId())
                    .itemName(item.getItemName())
                    .itemCost(item.getItemCost())
                    .itemCategory(item.getItemCategory())
                    .itemStatus(item.getItemStatus())
                    .itemImageName(item.getItemImage())
                    .build();
        }).toList();
    }

        public void frame_wearing(Long userId, Long itemId) {
            itemUserRepository.clearEquippedItems(userId, "프레임");

            ItemUserEntity itemToWear = itemUserRepository.findByUserIdAndItem_ItemId(userId, itemId);
            if (itemToWear != null) {
                itemToWear.setUsertemEquip("Y");
                itemUserRepository.save(itemToWear);
            } else {
                System.err.println("오류: 사용자 ID " + userId + "는 아이템 ID " + itemId + "를 소유하고 있지 않습니다.");
            }
        }
    }

