package com.example.PetLog.Likes;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LikesServiceImp implements LikesService{

    @Autowired
    LikesRepository likesRepository;


}
