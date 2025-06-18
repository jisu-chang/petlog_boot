package com.example.PetLog.Diary;

import com.example.PetLog.Pet.PetDTO;
import com.example.PetLog.Pet.PetEntity;
import com.example.PetLog.Pet.PetRepository;
import com.example.PetLog.Pet.PetService;
import com.example.PetLog.User.UserEntity;
import com.example.PetLog.User.UserService;
import jakarta.servlet.http.HttpSession;
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
import java.security.Principal;
import java.util.List;

@Controller
public class DiaryController {

    @Autowired
    DiaryService diaryService;

    @Autowired
    PetService petService;

    @Autowired
    UserService userService;

    @Autowired
    PetRepository petRepository;

    String path = System.getProperty("user.dir") + "/src/main/resources/static/image";

    @GetMapping (value = "/Diary/DiaryInput")
    public String in(Model mo, HttpSession hs, Principal principal) {

        String loginId = principal.getName();
        Long userId = userService.findUserIdByLoginId(loginId);

        List<PetDTO> list = petService.findPetsByUserId(userId);
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

        MultipartFile file = dto.getDiaryImage();
        if (file != null && !file.isEmpty()) {
            String filename = file.getOriginalFilename();

            // 저장 경로: 현재 프로젝트 위치 + static/image
            String path = System.getProperty("user.dir") + "/src/main/resources/static/image";
            File dir = new File(path);
            if (!dir.exists()) {
                dir.mkdirs(); // 폴더 없으면 생성
            }

            // 실제 저장
            file.transferTo(new File(dir, filename));
            dto.setDiaryImageName(filename); // DTO에 파일명 저장
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

        List<DiaryDTO> diaryList = diaryService.findDiaryByUserId(userId);

        mo.addAttribute("list", diaryList);

        return "Diary/DiaryOut";
    }

    @GetMapping(value = "/Diary/DiaryDetail")
    public String det(Model mo, @RequestParam("diaryId") long diaryId) {
        DiaryDTO diaryEntity = diaryService.detail(diaryId);

        if (diaryEntity == null) {
            return "redirect:/Diary/DiaryOut"; // 또는 에러 페이지
        }

        mo.addAttribute("dto", diaryEntity);
        return "Diary/DiaryDetail";
    }

    @GetMapping(value = "/Diary/DiaryUpdate")
    public String up(@RequestParam("diaryId") Long diaryId, Model mo, Principal principal) {
        if (principal == null) return "redirect:/login";

        DiaryEntity diaryEntity = diaryService.detailEntity(diaryId);
        PetEntity petEntity = petRepository.findById(diaryEntity.getPetId()).orElseThrow();

        DiaryDTO dto = new DiaryDTO();
        dto.setDiaryId(diaryEntity.getDiaryId());
        dto.setDiaryTitle(diaryEntity.getDiaryTitle());
        dto.setDiaryDate(diaryEntity.getDiaryDate());
        dto.setDiaryImageName(diaryEntity.getDiaryImage());
        dto.setDiaryContent(diaryEntity.getDiaryContent());
        dto.setPetName(petEntity.getPetName());
        dto.setUserId(diaryEntity.getUserId());
        dto.setPetId(diaryEntity.getPetId());

        mo.addAttribute("dto", dto);
        mo.addAttribute("pet", petEntity);

        return "Diary/DiaryUpdate";
    }

    @PostMapping(value = "/UpdateSave")
    public String save(@ModelAttribute DiaryDTO dto,
                       @RequestParam("himage") String himage,
                       Principal principal) throws IOException {

        MultipartFile mf = dto.getDiaryImage();
        File dir = new File(path);
        if (!dir.exists()) dir.mkdirs();

        if (mf != null && !mf.isEmpty()) {
            String filename = mf.getOriginalFilename();
            mf.transferTo(new File(path + File.separator + filename));
            dto.setDiaryImageName(filename);
        } else {
            dto.setDiaryImageName(himage);
        }

        // 유저 ID 세팅 (보안상 세션 또는 Principal로 처리 추천)
        String loginId = principal.getName();
        Long userId = userService.findUserIdByLoginId(loginId);
        dto.setUserId(userId);

        // 다이어리 업데이트
        DiaryEntity entity = dto.entity();
        diaryService.update(entity);

        return "redirect:/Diary/DiaryOut";
    }

    @GetMapping("/Diary/DiaryDelete")
    public String delete(@RequestParam("delete") Long diaryId, Model mo) {
        DiaryDTO dto = diaryService.detail(diaryId);  // DTO에 이미지명 포함되어 있음
        mo.addAttribute("dto", dto);
        return "Diary/DiaryDelete";
    }

    @PostMapping("/Delete")
    public String dd(@RequestParam("diaryId") Long diaryId, @RequestParam("himage") String image1) {

        diaryService.delete(diaryId);
        File file = new File(path,image1);
        if (file.exists()) file.delete();

        return "redirect:/Diary/DiaryOut";
    }

}
