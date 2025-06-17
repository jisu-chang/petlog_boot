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
}
