package com.example.PetLog.Comments;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class CommentsController {

    @Autowired
    CommentsService commentsService;


}
