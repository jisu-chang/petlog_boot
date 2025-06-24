package com.example.PetLog.Item;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ItemService {
    void save(ItemEntity itemEntity);

    List<ItemDTO> out();

    ItemEntity up(Long itemId);

    void update(ItemEntity entity);

    List<ItemDTO> itemStopped();

    void changeStatus(Long itemId);

    void changeStatusSell(Long itemId);

}
