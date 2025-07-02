package com.example.PetLog.ItemUser;

import com.example.PetLog.Item.ItemDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ItemUserService {
    List<ItemDTO> findFrameItemsByUserId(Long userId);

    void frame_wearing(Long userId, Long itemId);

    void deleteByUserId(Long userId);

    List<ItemUserEntity> findByUserId(Long userId);
}
