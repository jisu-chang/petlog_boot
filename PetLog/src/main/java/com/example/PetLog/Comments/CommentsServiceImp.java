package com.example.PetLog.Comments;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentsServiceImp implements CommentsService{

    @Autowired
    CommentsRepository commentsRepository;


    @Override
    public List<CommentsEntity> findByUserId(Long userId) {
        return commentsRepository.findAllByUser_UserId(userId);
    }

    @Override
    public void deleteByUserId(Long userId) {
        commentsRepository.deleteByUser_UserId(userId);
    }
}
