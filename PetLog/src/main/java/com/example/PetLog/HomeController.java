package com.example.PetLog;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping(value = "/")
    public String home() {
        return "main";
    }

    @GetMapping(value = "/main")
    public String home1() {
        return "main";
    }


}
