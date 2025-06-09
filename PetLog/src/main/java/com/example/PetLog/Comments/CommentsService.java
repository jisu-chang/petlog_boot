package com.example.PetLog.Comments;

import java.util.List;

public interface CommentsService {

    List<CommentsEntity> findByUserId(Long userId);

    void deleteByUserId(Long userId);
}
