package com.example.PetLog.Item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ItemServiceImp implements ItemService {

    @Autowired
    ItemRepository itemRepository;

    @Override
    public void save(ItemEntity itemEntity) {
        itemRepository.save(itemEntity);
    }

    @Override
    public List<ItemDTO> out() {
        List<ItemEntity> list = itemRepository.findAll();

        return list.stream().map(entity -> {
            ItemDTO dto = new ItemDTO();
            dto.setItemId(entity.getItemId());
            dto.setItemName(entity.getItemName());
            dto.setItemCost(entity.getItemCost());
            dto.setItemCategory(entity.getItemCategory());
            dto.setItemImageName(entity.getItemImage());
            dto.setItemStatus(entity.getItemStatus());
            return dto;  // 반환 부분 추가
        }).toList();
    }

}
