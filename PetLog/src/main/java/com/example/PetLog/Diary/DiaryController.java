package com.example.PetLog.Diary;

import com.example.PetLog.Pet.PetDTO;
import com.example.PetLog.Pet.PetEntity;
import com.example.PetLog.Pet.PetRepository;
import com.example.PetLog.Pet.PetService;
import com.example.PetLog.User.UserEntity; // UserEntity 임포트 추가 (UserService에서 UserEntity 반환 시 필요)
import com.example.PetLog.User.UserService;
import jakarta.servlet.http.HttpSession; // HttpSession 임포트 추가
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

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

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

    String path = "C:/petlog-uploads/Diary";

    @GetMapping (value = "/Diary/DiaryInput")
    public String in(Model mo, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        String userLoginId = (String) session.getAttribute("userLoginId");

        if (userId == null || userLoginId == null) {
            return "redirect:/login";
        }

        List<PetDTO> list = petService.findPetsByUserId(userId);
        mo.addAttribute("list",list);
        mo.addAttribute("petCount", list.size());

        return "Diary/DiaryInput";
    }

    @PostMapping("/DiarySave")
    public String saveDiary(DiaryDTO dto,HttpSession session) throws IOException {
        Long userId = (Long) session.getAttribute("userId");
        String userLoginId = (String) session.getAttribute("userLoginId");

        if (userId == null || userLoginId == null) {
            return "redirect:/login";
        }

        dto.setUserId(userId);

        MultipartFile file = dto.getDiaryImage();
        if (file != null && !file.isEmpty()) {
            String filename = file.getOriginalFilename();

            File dir = new File(path);
            if (!dir.exists()) {
                dir.mkdirs(); // 폴더 없으면 생성
            }

            file.transferTo(new File(dir, filename));
            dto.setDiaryImageName(filename);
        }

        DiaryEntity diaryEntity = dto.entity();
        diaryService.save(diaryEntity);

        userService.findUserByLoginId(userId.toString()).ifPresent(updatedUser -> {
            session.setAttribute("grapeCount", updatedUser.getGrapeCount()); // 세션에 최신 grapeCount 저장
            System.out.println("세션의 포도알 개수 업데이트: " + updatedUser.getGrapeCount()); // 콘솔 로그로 확인
        });

        return "redirect:/Diary/DiaryInput?success=grape";
    }

    @GetMapping(value = "/Diary/DiaryOut")
    public String out(Model mo,
                      HttpSession session,
                      @RequestParam(defaultValue = "0") int page) {
        Long userId = (Long) session.getAttribute("userId");
        String userLoginId = (String) session.getAttribute("userLoginId");

        if (userId == null || userLoginId == null) {
            return "redirect:/login";
        }

        //page
        int pageSize = 5; //한 페이지에 5 게시물
        Pageable pageable = PageRequest.of(page, pageSize, Sort.by("diaryDate").descending());
        Page<DiaryDTO> diaryPage = diaryService.findDiaryByUserIdPaged(userId, pageable);

        mo.addAttribute("diaryPage", diaryPage);
        mo.addAttribute("currentPage", page);
        mo.addAttribute("totalPages", diaryPage.getTotalPages());

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
    public String up(@RequestParam("diaryId") Long diaryId, Model mo, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        String userLoginId = (String) session.getAttribute("userLoginId");

        if (userId == null || userLoginId == null) {
            return "redirect:/login";
        }

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