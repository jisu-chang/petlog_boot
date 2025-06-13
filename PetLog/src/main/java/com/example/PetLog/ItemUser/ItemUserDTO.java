package com.example.PetLog.ItemUser;

import com.example.PetLog.Item.ItemEntity;
import com.example.PetLog.Item.ItemRepository;
import com.example.PetLog.User.UserEntity;
import com.example.PetLog.User.UserRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ItemUserDTO {

    Long usertemId;
    String usertemEquip;

    Long itemId;
    Long userId;

    String itemName;
    String itemCategory;
    String itemImageName;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    // DTO를 Entity로 변환하는 메서드
    public ItemUserEntity toEntity() {
        // itemId를 이용하여 ItemEntity를 가져옴
        ItemEntity item = itemRepository.findById(itemId).orElse(null);

        // userId를 이용하여 UserEntity를 가져옴
        UserEntity user = userRepository.findById(userId).orElse(null);

        // 만약 item이나 user가 없으면 예외 처리
        if (item == null) {
            throw new IllegalArgumentException("Item not found with id: " + itemId);
        }
        if (user == null) {
            throw new IllegalArgumentException("User not found with id: " + userId);
        }

        // Entity 객체 생성하여 반환
        return ItemUserEntity.builder()
                .item(item)  // itemId로 찾아온 ItemEntity를 설정
                .user(user)  // userId로 찾아온 UserEntity를 설정
                .usertemEquip(usertemEquip)  // usertemEquip 그대로 설정
                .build();
    }

}