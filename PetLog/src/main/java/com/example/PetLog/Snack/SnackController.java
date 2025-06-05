package com.example.PetLog.Snack;

import com.example.PetLog.Pet.PetDTO;
import com.example.PetLog.User.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
        hs.setAttribute("userId", userId);

        return "Snack/SnackInput";
    }

    @PostMapping(value = "/SnackSave")
    public String save(SnackDTO dto, Principal pri) throws IOException {

        String loginId = pri.getName();
        Long userId = userService.findUserIdByLoginId(loginId);

        dto.setUserId(userId);

        MultipartFile file = dto.getSnackImage();
        if (file != null && !file.isEmpty()) {
            String filename = file.getOriginalFilename();

            String path = System.getProperty("user.dir") + "/src/main/resources/static/image";
            File dir = new File(path);
            if (!dir.exists()) {
                dir.mkdirs(); // 폴더 없으면 생성
            }

            // ✅ 이미지 저장과 DTO에 파일명 설정은 반드시 if 밖에서 실행돼야 함
            file.transferTo(new File(dir, filename));
            dto.setSnackImagename(filename);
        }

        SnackEntity snackEntity = dto.entity();  // 이미지 이름까지 포함된 entity로 변환
        snackService.save(snackEntity);

        return "redirect:/Snack/SnackInput";
    }

    @GetMapping(value = "/Snack/SnackOut")
    public String out(Model mo, Principal pri) {

        String loginId = pri.getName();
        Long userId = userService.findUserIdByLoginId(loginId);

        List<SnackDTO> list = snackService.out();

        mo.addAttribute("list",list);

        return "Snack/SnackOut";
    }

    @GetMapping(value = "Snack/SnackDetail")
    public String detail(Model mo, @RequestParam("snackId")long snackId) {

        SnackDTO snackDTO = snackService.detail(snackId);
        if (snackDTO == null) {
            return "redirect:/Snack/SnackOut"; // 또는 에러 페이지
        } //snackId 못 찾으면 여기로 redirect

        System.out.println("snackDTO = " + snackDTO);
        mo.addAttribute("dto",snackDTO);

        return "Snack/SnackDetail";
    }




    @PostMapping(value = "/SnackUpdateSave")
    public String us() {

        return "";
    }

}