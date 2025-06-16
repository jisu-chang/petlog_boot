package com.example.PetLog.ItemUser;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ItemUserDTO {

    Long usertemId;
    String usertemEquip;

    Long itemId;
    Long userId;

    String itemName;
    String itemCategory;
    String itemImageName;

    public ItemUserEntity toEntity() {
        return ItemUserEntity.builder()
                .itemId(itemId)
                .userId(userId)
                .usertemEquip(usertemEquip)
                .build();
    }

}
