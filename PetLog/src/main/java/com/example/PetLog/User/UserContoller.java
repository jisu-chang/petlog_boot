package com.example.PetLog.User;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.io.File;
import java.io.IOException;

@Slf4j
@Controller
public class UserContoller {

    @Autowired
    UserService userService;
    String path="C:\\MBC12AI\\spring_boot\\PetLog\\src\\main\\resources\\static\\image";


    @GetMapping(value = "/login")
    public String loginpage(){
        return "User/UserLogin";
    }

    @GetMapping("/signUp")
    public String signUp(Model model){
        model.addAttribute("userDTO",new UserDTO());
        return "User/UserSignUp";
    }

    @PostMapping("/signUpSave")
    public String member2(@ModelAttribute("userDTO") @Valid UserDTO userDTO , BindingResult bindingResult, MultipartHttpServletRequest mul) throws IOException {
        if(bindingResult.hasErrors()){
            //유효성검시시 에러가 있으면
            return "User/UserSignUp";//입력폼 다시 호출
        }
        else {
            MultipartFile mf=mul.getFile("profileimg");
            mf.transferTo(new File(path+"\\"+mf.getOriginalFilename()));
            userService.signUpInsert(userDTO); //서비스로 데이터 보냄
            return "redirect:/main";
        }
    }

}
