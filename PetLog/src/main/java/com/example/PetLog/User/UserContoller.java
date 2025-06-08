package com.example.PetLog.User;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.io.File;
import java.io.IOException;

@Slf4j
@Controller
public class UserContoller {

    @Autowired
    UserService userService;

    String path = new File("src/main/resources/static/image").getAbsolutePath();

    @GetMapping(value = "/login")
    public String loginpage(){
        return "User/UserLogin";
    }

    @PostMapping("/login")
    public String login(@RequestParam("userId") Long userId, HttpSession session) {
        // 로그인 성공 시 세션에 user_id 저장
        session.setAttribute("user_id", userId);
        return "redirect:/"; // 로그인 후 홈 페이지로 리디렉션
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

    @GetMapping("/MyPage")
    public String MyPage(Model mo, HttpSession session) {
        // 세션에서 로그인한 사용자 ID 가져오기
        Long userId = (Long) session.getAttribute("user_id");

        // 로그인되지 않은 경우 로그인 페이지로 리디렉트
        if (userId == null) {
            log.info("No user_id in session, redirecting to login page");
            return "redirect:/login";
        }

        log.info("User is logged in with user_id: " + userId); // 로그 추가

        // UserService를 사용하여 userId로 UserEntity 객체 가져오기
        UserEntity userEntity = userService.findById(userId);  // userDTO가 아닌 userId 사용

        // UserEntity 객체를 모델에 추가하여 뷰에 전달
        mo.addAttribute("list", userEntity);
        return "User/UserMyPage";
    }

    @GetMapping("/UserUpdate")
    public String userUpdate(HttpSession session, Model mo){
        Long userId = (Long) session.getAttribute("user_id");
        UserEntity udto = userService.updateById(userId);
        mo.addAttribute("dto", udto);
        return "User/UserUpdate";
    }

    @PostMapping("/UserUpdateSave")
    public String userUpdateSave(UserDTO userDTO,@RequestParam("profileimg") MultipartFile mf, @RequestParam("dfname") String dfname, HttpSession session, Model mo) throws IOException {
        Long userId = (Long) session.getAttribute("user_id");
        //기존 유저 정보 가져오기
        UserEntity userEntity = userService.findById(userId);
        //기존 비밀번호 유지
        userDTO.setPassword(userEntity.getPassword());

        new File(path+"\\"+dfname).delete();
        mf.transferTo(new File(path+"\\"+mf.getOriginalFilename()));

        userDTO.setUser_id(userId);
        userService.updatesave(userDTO.toEntity());
        return "redirect:/MyPage";
    }
}
