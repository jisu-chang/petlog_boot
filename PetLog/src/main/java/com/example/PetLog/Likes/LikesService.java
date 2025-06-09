package com.example.PetLog.Likes;

import java.util.List;

public interface LikesService {
    List<LikesEntity> findByUserId(Long userId);

    void deleteByUserId(Long userId);
}
