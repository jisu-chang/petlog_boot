package com.example.PetLog.Likes;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class LikesController {

    @Autowired
    LikesService likesService;


}
