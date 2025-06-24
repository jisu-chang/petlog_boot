package com.example.PetLog.Snack;

import com.example.PetLog.Comments.CommentsDTO;
import com.example.PetLog.Comments.CommentsEntity;
import com.example.PetLog.Comments.CommentsService;
import com.example.PetLog.Likes.LikesService;
import com.example.PetLog.User.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.IOException;
import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class SnackController {

    @Autowired
    SnackService snackService;
    @Autowired
    UserService userService;
    @Autowired
    LikesService likesService;
    @Autowired
    CommentsService commentsService;

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
    public String save(SnackDTO dto, HttpSession session) throws IOException {
        // 로그인 한 유저 정보 가져옴
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";  // 로그인 페이지로 리다이렉트
        }
//        Long userId = userService.findUserIdByLoginId(loginId);

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

        return "redirect:/Snack/SnackOut";
    }

    @GetMapping(value = "/Snack/SnackOut")
    public String out(Model mo, Principal pri) {

        String loginId = pri.getName();
        Long userId = userService.findUserIdByLoginId(loginId);

        List<SnackDTO> list = snackService.out();
        // 댓글수 / 좋아요수 매핑
        Map<Long, Integer> commentCounts = new HashMap<>();
        Map<Long, Integer> likeCounts = new HashMap<>();

        mo.addAttribute("commentCounts", commentCounts);
        mo.addAttribute("likeCounts", likeCounts);
        mo.addAttribute("list",list);
        return "Snack/SnackOut";
    }

    @GetMapping(value = "Snack/SnackDetail")
    public String detail(Model mo, @RequestParam("snackId")long snackId, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        String userLoginId =(String) session.getAttribute("userLoginId");

        SnackDTO snackDTO = snackService.detail(snackId);
        if (snackDTO == null) {
            return "redirect:/Snack/SnackOut"; // 또는 에러 페이지
        } //snackId 못 찾으면 여기로 redirect



        System.out.println("snackDTO = " + snackDTO);
        mo.addAttribute("dto", snackDTO);

        return "Snack/SnackDetail";
    }

    @GetMapping(value = "/Snack/SnackUpdate")
    public String up(@RequestParam("snackId")Long snackId, Model mo, Principal pri) {

        if (pri == null) return "redirect:/login";

        SnackEntity snackEntity = snackService.getSnack(snackId);

        SnackDTO dto = new SnackDTO();
        dto.setSnackId(snackEntity.getSnackId());
        dto.setSnackTitle(snackEntity.getSnackTitle());
        dto.setSnackRecipe(snackEntity.getSnackRecipe());
        dto.setSnackImagename(snackEntity.getSnackImage()); // 이미지 이름
        dto.setSnackDate(snackEntity.getSnackDate());
        dto.setSnackReadcnt(snackEntity.getSnackReadcnt());
        //dto.setGetGrape(snackEntity.getGetGrape());
        dto.setUserId(snackEntity.getUserId());

        mo.addAttribute("dto", dto);

        return "Snack/SnackUpdate";
    }

    @PostMapping(value = "/SnackUpdateSave")
    public String us(@ModelAttribute SnackDTO dto,@RequestParam("himage") String himage, HttpSession hs, Principal pri) throws IOException {

        MultipartFile mf = dto.getSnackImage();
        File dir = new File(path);
        if (!dir.exists()) dir.mkdirs();

        if (mf != null && !mf.isEmpty()) {
            String filename = mf.getOriginalFilename();
            mf.transferTo(new File(path + File.separator + filename));
            dto.setSnackImagename(filename);
        } else {
            dto.setSnackImagename(himage);
        }

        String loginId = pri.getName();
        Long userId = userService.findUserIdByLoginId(loginId);
        dto.setUserId(userId);

        SnackEntity entity = dto.entity();
        snackService.update(entity);

        return "redirect:/Snack/SnackOut";
    }

    @GetMapping(value = "/Snack/SnackDelete")
    public String dd(@RequestParam("delete")Long snackId, Model mo) {

        SnackDTO dto = snackService.detail(snackId);
        mo.addAttribute("dto",dto);

        return "Snack/SnackDelete";
    }

    @PostMapping(value = "/DeleteSnack")
    public String dd2(@RequestParam("snackId")Long snackId,@RequestParam("himage")String imagename) {

        snackService.delete(snackId);
        File file = new File(path, imagename);
        if(file.exists()) file.delete();

        return "redirect:/Snack/SnackOut";
    }


}