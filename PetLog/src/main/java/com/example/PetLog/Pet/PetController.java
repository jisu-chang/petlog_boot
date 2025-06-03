package com.example.PetLog.Pet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Controller
public class PetController {

    //DB user_id 널이 아님으로 꼭 수정해야함!!!!!!

    @Autowired
    PetService petService;

    String path = "C:\\1MBC\\SpringBoot\\PetLog\\src\\main\\resources\\static\\image";

    @GetMapping(value = "/Pet/PetInput")
    public String input(@ModelAttribute PetDTO petDTO) {
        //(@ModelAttribute PetDTO petDTO) <- 이거 해당 form에 입력하기 위해 쓰는 거임 ex) name이면 PetName칸에..
        return "Pet/PetInput";
    }

    @PostMapping(value = "/PetSave")
    public String save(PetDTO petDTO, @RequestParam("petImg") MultipartFile mf) throws IOException {

        // ✅ 1. 파일 저장
        String fileName = mf.getOriginalFilename();
        if (!mf.isEmpty()) {
            mf.transferTo(new File(path + "\\" + fileName));
        }

        // ✅ 2. DTO에 꼭 넣어줘야 entity()에서 파일명이 들어감
        petDTO.setPetImg(mf); // ✨ 이 한 줄이 핵심입니다!!

        // ✅ 3. entity 저장
        PetEntity petEntity = petDTO.entity();
        petService.save(petEntity);

        return "redirect:/Pet/PetInput";
    }

    @GetMapping(value = "/Pet/PetOut")
    public String out(Model mo) {

        List<PetEntity> list = petService.petOut();
        mo.addAttribute("list",list);

        return "Pet/PetOut";
    }

    @GetMapping(value = "/Pet/PetDetail")
    public String de(Model mo, @RequestParam("petId") long petId) {

        PetEntity petEntity = petService.detail(petId);
        mo.addAttribute("petEntity",petEntity);

        return "Pet/PetDetail";
    } //404 오류 뜸!!
}
