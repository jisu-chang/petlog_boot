package com.example.PetLog.Item;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ItemDTO {

    Long itemId;
    String itemName;
    Long itemCost;
    String itemCategory;
    MultipartFile itemImage;
    String itemStatus;
    String itemImageName;

    public ItemEntity entity() {
        return ItemEntity.builder()
                .itemId(itemId)
                .itemName(itemName)
                .itemCost(itemCost)
                .itemCategory(itemCategory)
                .itemImage(itemImageName)
                .itemStatus(itemStatus)
                .build();
    }
}
