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

    @Autowired
    PetService petService;

    // ✅ 상대경로로 변경 (프로젝트 안의 static/image 폴더)
    String path = new File("src/main/resources/static/image").getAbsolutePath();

    @GetMapping(value = "/Pet/PetInput")
    public String input(@ModelAttribute PetDTO petDTO) {
        return "Pet/PetInput";
    }

    @PostMapping(value = "/PetSave")
    public String save(PetDTO petDTO, @RequestParam("petImg") MultipartFile mf) throws IOException {

        // ✅ 폴더 없으면 만들기
        File dir = new File(path);
        if (!dir.exists()) dir.mkdirs();

        // ✅ 파일 저장
        String fileName = mf.getOriginalFilename();
        if (!mf.isEmpty()) {
            mf.transferTo(new File(path + File.separator + fileName));
        }

        // ✅ DTO에 이미지 정보 담기
        petDTO.setPetImg(mf);

        // ✅ 저장
        PetEntity petEntity = petDTO.entity();
        petService.save(petEntity);

        return "redirect:/Pet/PetInput";
    }

    @GetMapping(value = "/Pet/PetOut")
    public String out(Model mo) {
        List<PetEntity> list = petService.petOut();
        mo.addAttribute("list", list);
        return "Pet/PetOut";
    }

    @GetMapping(value = "/Pet/PetDetail")
    public String de(Model mo, @RequestParam("petId") long petId) {
        PetEntity petEntity = petService.detail(petId);
        mo.addAttribute("petEntity", petEntity);
        return "Pet/PetDetail";
    }

    @GetMapping(value = "/Pet/PetUpdate")
    public String up(@RequestParam("update") Long petId, Model mo) {
        PetEntity petEntity = petService.detail(petId);
        mo.addAttribute("petEntity", petEntity);
        return "Pet/PetUpdate";
    }

    @PostMapping(value = "/PetUpdateSave")
    public String up2(@ModelAttribute PetDTO petDTO, @RequestParam("himage") String himage) throws IOException {

        MultipartFile mf = petDTO.getPetImg();

        // ✅ 폴더 없으면 만들기
        File dir = new File(path);
        if (!dir.exists()) dir.mkdirs();

        // 새 이미지가 업로드되었는지 확인
        if (mf != null && !mf.isEmpty()) {
            String filename = mf.getOriginalFilename();
            mf.transferTo(new File(path + File.separator + filename));
            petDTO.setPetImgName(filename);  // 새 파일명 저장
        } else {
            petDTO.setPetImgName(himage);    // 이전 파일명 유지
        }

        PetEntity petEntity = petDTO.entity();
        petService.update(petEntity);

        return "redirect:/Pet/PetDetail?petId=" + petDTO.getPetId();
    }
}