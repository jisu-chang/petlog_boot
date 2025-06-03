package com.example.PetLog.Diary;

import com.example.PetLog.Pet.PetDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Controller
public class DiaryController {

    @Autowired
    DiaryService diaryService;

    @GetMapping (value = "/Diary/DiaryInput")
    public String in(Model mo) {

        //유저별 반려동물 목록 수정필요!!!
        List<PetDTO> list = new ArrayList<>();
        mo.addAttribute("list",list);

        return "Diary/DiaryInput";
    }

    @PostMapping(value = "/DiarySave")
    public String sa(DiaryDTO dto) throws IOException {
        //DiarySave는 html파일 없어서 앞에 폴더명 쓰면 안됨!

        MultipartFile file = dto.getDiaryImage();

        if (file != null && !file.isEmpty()) {
            String filename = file.getOriginalFilename();
            String path = "src/main/resources/static/image/";

            file.transferTo(new File(path + filename));
        }

        DiaryEntity diaryEntity = dto.entity();
        diaryService.save(diaryEntity);

        return "redirect:/Diary/DiaryInput";
    }
}
