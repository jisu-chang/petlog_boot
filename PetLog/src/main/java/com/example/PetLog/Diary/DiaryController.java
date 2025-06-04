package com.example.PetLog.Diary;

import com.example.PetLog.Pet.PetDTO;
import com.example.PetLog.Pet.PetService;
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
import java.util.ArrayList;
import java.util.List;

@Controller
public class DiaryController {

    @Autowired
    DiaryService diaryService;

    @Autowired
    PetService petService;

    @Autowired
    UserService userService;

    @GetMapping (value = "/Diary/DiaryInput")
    public String in(Model mo, HttpSession hs, Principal principal) {

        String loginId = principal.getName();
        Long userId = userService.findUserIdByLoginId(loginId);

        List<PetDTO> list = petService.findPetById(userId);
        mo.addAttribute("list",list);
        mo.addAttribute("petCount", list.size());

        return "Diary/DiaryInput";
    }

    @PostMapping("/DiarySave")
    public String saveDiary(DiaryDTO dto, Principal principal) throws IOException {
        // 1. 로그인 아이디로 userId 조회
        String loginId = principal.getName();
        Long userId = userService.findUserIdByLoginId(loginId);

        // 2. DTO에 userId 세팅
        dto.setUserId(userId);

        // 3. 이미지 저장 (서버가 인식하는 경로로 저장!)
        MultipartFile file = dto.getDiaryImage();
        if (file != null && !file.isEmpty()) {
            String filename = file.getOriginalFilename();
            
            String path = new File("src/main/resources/static/image").getAbsolutePath();
            File dir = new File(path);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            file.transferTo(new File(dir, filename));

            // 4. DTO에 파일명 저장
            dto.setDiaryImageName(filename);
        }

        // 5. Entity로 변환 후 저장
        DiaryEntity diaryEntity = dto.entity();
        diaryService.save(diaryEntity);

        // 6. 리다이렉트
        return "redirect:/Diary/DiaryInput";
    }

    @GetMapping(value = "/Diary/DiaryOut")
    public String out(Model mo, Principal principal) {

        String loginId = principal.getName();
        Long userId = userService.findUserIdByLoginId(loginId);

        // 페이징 없이 전부 조회
        List<DiaryDTO> diaryList = diaryService.findDiaryByUserId(userId);

        mo.addAttribute("list", diaryList);

        return "Diary/DiaryOut";
    }

}
