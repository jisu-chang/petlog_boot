package com.example.PetLog.Item;

import com.example.PetLog.ItemUser.ItemUserRepository;
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

        List<ItemEntity> list = itemRepository.findByItemStatus("판매중");

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

    @Override
    public ItemEntity up(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(()->new RuntimeException("아이템을 찾을 수 없습니다: " + itemId));
    }

    @Override
    public void update(ItemEntity entity) {
        itemRepository.save(entity);
    }

    @Override
    public List<ItemDTO> itemStopped() {
        List<ItemEntity> stoppedItems = itemRepository.findByItemStatus("판매종료");

        return stoppedItems.stream().map(entity -> {
            ItemDTO dto = new ItemDTO();
            dto.setItemId(entity.getItemId());
            dto.setItemName(entity.getItemName());
            dto.setItemCost(entity.getItemCost());
            dto.setItemCategory(entity.getItemCategory());
            dto.setItemImageName(entity.getItemImage());
            dto.setItemStatus(entity.getItemStatus());
            return dto;
        }).toList();

    }

    @Override
    public void changeStatus(Long itemId) {
        ItemEntity itemEntity = itemRepository.findById(itemId).orElseThrow(() -> new RuntimeException("아이템을 찾을 수 없습니다: " + itemId));
        itemEntity.setItemStatus("판매종료");
        itemRepository.save(itemEntity);
    }

    @Override
    public void changeStatusSell(Long itemId) {

        // 아이템을 ID로 찾아서 상태 변경
        ItemEntity item = itemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("아이템을 찾을 수 없습니다: " + itemId));

        // 상태를 '판매중'으로 설정
        item.setItemStatus("판매중");

        // 상태 변경 후 저장
        itemRepository.save(item);
    }
}
