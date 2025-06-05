package com.example.PetLog.Comments;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CommentsServiceImp implements CommentsService{

    @Autowired
    CommentsRepository commentsRepository;


}
