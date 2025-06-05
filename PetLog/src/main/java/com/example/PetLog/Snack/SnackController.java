package com.example.PetLog.Snack;

import com.example.PetLog.Pet.PetDTO;
import com.example.PetLog.User.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.security.Principal;
import java.util.List;

@Controller
public class SnackController {

    @Autowired
    SnackService snackService;

    @Autowired
    UserService userService;

    String path = System.getProperty("user.dir") + "/src/main/resources/static/image";

    @GetMapping(value = "/Snack/SnackInput")
    public String input(Model mo, HttpSession hs, Principal principal) {

        String loginId = principal.getName();
        Long userId = userService.findUserIdByLoginId(loginId);

        mo.addAttribute("userId", userId);
        hs.setAttribute("user_id", userId);

        return "Snack/SnackInput";
    }

    @PostMapping(value = "/SnackSave")
    public String save(SnackDTO dto, Principal pri) throws IOException {

        String loginId = pri.getName();
        Long userId = userService.findUserIdByLoginId(loginId);

        dto.setUser_id(userId);

        MultipartFile file = dto.getSnack_image();
        if (file != null && !file.isEmpty()) {
            String filename = file.getOriginalFilename();

            String path = System.getProperty("user.dir") + "/src/main/resources/static/image";
            File dir = new File(path);
            if (!dir.exists()) {
                dir.mkdirs(); // 폴더 없으면 생성

                file.transferTo(new File(dir, filename));
                dto.setSnack_imagename(filename); // DTO에 파일명 저장
            }

            SnackEntity snackEntity = dto.entity();
            snackService.save(snackEntity);
        }
        return "redirect:/Snack/SnackInput";
    }

}