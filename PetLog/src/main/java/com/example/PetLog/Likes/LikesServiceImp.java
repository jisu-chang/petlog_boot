package com.example.PetLog.Likes;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LikesServiceImp implements LikesService{

    @Autowired
    LikesRepository likesRepository;


    @Override
    public List<LikesEntity> findByUserId(Long userId) {
        return likesRepository.findAllByUser_UserId(userId);
    }

    @Override
    public void deleteByUserId(Long userId) {
        likesRepository.deleteByUser_UserId(userId);
    }
}
