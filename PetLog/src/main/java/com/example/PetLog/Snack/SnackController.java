package com.example.PetLog.Snack;

import com.example.PetLog.Comments.CommentsDTO;
import com.example.PetLog.Comments.CommentsEntity;
import com.example.PetLog.Comments.CommentsService;
import com.example.PetLog.Likes.LikesService;
import com.example.PetLog.Quiz.QuizService;
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
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

@Controller
public class SnackController {

    @Autowired
    private SnackService snackService;
    @Autowired
    private UserService userService;
    @Autowired
    private CommentsService commentsService;
    @Autowired
    private LikesService likesService;

    String path = "C:/petlog-uploads/snack";

    // 입력 폼
    @GetMapping("/Snack/SnackInput")
    public String input(Model model, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        userService.findByuserId(userId);

        model.addAttribute("userId", userId);
        session.setAttribute("userId", userId);

        return "Snack/SnackInput";
    }

    // 저장
    @PostMapping("/SnackSave")
    public String save(@ModelAttribute SnackDTO dto, HttpSession session) throws IOException {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }

        dto.setUserId(userId);
        dto.setSnackReadcnt(0);
        dto.setSnackDate(LocalDate.now());

        File uploadDir = new File(path);
        if (!uploadDir.exists()) uploadDir.mkdirs();

        MultipartFile file = dto.getSnackImage();
        if (file != null && !file.isEmpty()) {
            String filename = file.getOriginalFilename();
            file.transferTo(new File(uploadDir, filename));
            dto.setSnackImagename(filename);
        }

        snackService.save(dto.entity());

        return "redirect:/Snack/SnackOut";
    }

    // 리스트 + 페이징
    @GetMapping("/Snack/SnackOut")
    public String out(Model model, Principal principal,
                      @RequestParam(defaultValue = "0") int page,
                      @RequestParam(defaultValue = "5") int size) {

        String loginId = principal.getName();
        Long userId = userService.findUserIdByLoginId(loginId);

        Pageable pageable = PageRequest.of(page, size,
                Sort.by(Sort.Order.desc("snackDate"), Sort.Order.desc("snackId")));
        Page<SnackDTO> snackPage = snackService.findPagedSnacks(pageable);

        Map<Long, Integer> commentCounts = new HashMap<>();
        Map<Long, Integer> likeCounts = new HashMap<>();

        for (SnackDTO snack : snackPage.getContent()) {
            Long snackId = snack.getSnackId();
            commentCounts.put(snackId, snackService.getCommentCount(snackId));
            likeCounts.put(snackId, snackService.getLikeCount(snackId));
        }

        model.addAttribute("commentCounts", commentCounts);
        model.addAttribute("likeCounts", likeCounts);
        model.addAttribute("snackPage", snackPage);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", snackPage.getTotalPages());

        return "Snack/SnackOut";
    }

    // 상세보기
    @GetMapping("/Snack/SnackDetail")
    public String detail(Model model, @RequestParam("snackId") long snackId, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        String userLoginId = (String) session.getAttribute("userLoginId");

        SnackDTO snackDTO = snackService.detail(snackId);
        if (snackDTO == null) {
            return "redirect:/Snack/SnackOut";
        }

        int likeCount = likesService.getSnackLikeCount(snackId);
        boolean likedByUser = likesService.isLikedByUserOnSnack(snackId, userId, userLoginId);
        List<CommentsEntity> comments = commentsService.getSnackComments(snackId);

        model.addAttribute("sessionUserId", userId);
        model.addAttribute("sessionUserLoginId", userLoginId);
        model.addAttribute("dto", snackDTO);
        model.addAttribute("likeCount", likeCount);
        model.addAttribute("likedByUser", likedByUser);
        model.addAttribute("comments", comments);

        return "Snack/SnackDetail";
    }

    // 수정폼
    @GetMapping("/Snack/SnackUpdate")
    public String up(@RequestParam("snackId") Long snackId, Model model, Principal principal) {
        if (principal == null) return "redirect:/login";

        SnackEntity snackEntity = snackService.getSnack(snackId);

        SnackDTO dto = new SnackDTO(snackEntity);
        dto.setSnackImagename(snackEntity.getSnackImage());

        model.addAttribute("dto", dto);

        return "Snack/SnackUpdate";
    }

    // 수정 저장
    @PostMapping("/SnackUpdateSave")
    public String us(@ModelAttribute SnackDTO dto, @RequestParam("himage") String himage,
                     HttpSession session, Principal principal) throws IOException {

        File uploadDir = new File(path);
        if (!uploadDir.exists()) uploadDir.mkdirs();

        MultipartFile mf = dto.getSnackImage();
        if (mf != null && !mf.isEmpty()) {
            String filename = mf.getOriginalFilename();
            mf.transferTo(new File(uploadDir, filename));
            dto.setSnackImagename(filename);
        } else {
            dto.setSnackImagename(himage);
        }

        SnackEntity original = snackService.getSnack(dto.getSnackId());
        dto.setSnackDate(original.getSnackDate());

        String loginId = principal.getName();
        Long userId = userService.findUserIdByLoginId(loginId);
        dto.setUserId(userId);

        snackService.update(dto.entity());

        return "redirect:/Snack/SnackOut";
    }

    // 삭제 폼
    @GetMapping("/Snack/SnackDelete")
    public String dd(@RequestParam("delete") Long snackId, Model model) {
        SnackDTO dto = snackService.detail(snackId);
        model.addAttribute("dto", dto);
        return "Snack/SnackDelete";
    }

    // 삭제 처리
    @PostMapping("/DeleteSnack")
    public String dd2(@RequestParam("snackId") Long snackId, @RequestParam("himage") String imagename) {
        snackService.delete(snackId);

//        File file = new File(path, imagename);
//        if (file.exists()) file.delete();

        return "redirect:/Snack/SnackOut";
    }

    // RESTful 스타일 상세 조회 (경로 변수)
    @GetMapping("/snack/{snackId}")
    public String viewSnack(@PathVariable Long snackId, HttpSession session, Model model) {
        Long userId = (Long) session.getAttribute("userId");
        String userLoginId = (String) session.getAttribute("userLoginId");

        SnackEntity snack = snackService.getSnackById(snackId);
        boolean likedByUser = likesService.isLikedByUserOnSnack(snackId, userId, userLoginId);
        int likeCount = likesService.getSnackLikeCount(snackId);
        List<CommentsEntity> comments = commentsService.getSnackComments(snackId);

        model.addAttribute("dto", snack);
        model.addAttribute("likedByUser", likedByUser);
        model.addAttribute("likeCount", likeCount);
        model.addAttribute("comments", comments);

        return "Snack/SnackDetail";
    }

    // 좋아요 처리
    @PostMapping("/snack/{snackId}/like")
    public String likeOnSnack(@PathVariable Long snackId, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        String userLoginId = (String) session.getAttribute("userLoginId");

        if (userId == null || userLoginId == null) {
            return "redirect:/login";
        }

        likesService.likeOnUserSnackId(snackId, userId, userLoginId);

        return "redirect:/Snack/SnackDetail?snackId=" + snackId;
    }

    // 댓글 저장
    @PostMapping("/snack/comment")
    public String saveComment(@ModelAttribute("commentsDTO") CommentsDTO commentsDTO,
                              RedirectAttributes redirectAttributes, HttpSession session) {

        Long userId = (Long) session.getAttribute("userId");
        String userLoginId = (String) session.getAttribute("userLoginId");

        if (userId == null || userLoginId == null) {
            return "redirect:/login";
        }

        commentsDTO.setUser_id(userId);
        commentsDTO.setUserLoginId(userLoginId);
        commentsService.saveComment(commentsDTO);

        if (commentsDTO.getSnack_id() != null) {
            redirectAttributes.addAttribute("snackId", commentsDTO.getSnack_id());
            return "redirect:/Snack/SnackDetail";
        } else {
            return "redirect:/Snack";
        }
    }

    // 검색 처리
    @GetMapping("/snackSearch")
    public String searchSnacks(@RequestParam("postType") String postType,
                               @RequestParam("keyword") String keyword,
                               Model model) {

        List<SnackDTO> searchResult = snackService.searchSnacks(postType, keyword);

        Map<Long, Integer> commentCounts = new HashMap<>();
        Map<Long, Integer> likeCounts = new HashMap<>();

        for (SnackDTO snack : searchResult) {
            Long snackId = snack.getSnackId();
            commentCounts.put(snackId, snackService.getCommentCount(snackId));
            likeCounts.put(snackId, snackService.getLikeCount(snackId));
        }

        model.addAttribute("list", searchResult);
        model.addAttribute("commentCounts", commentCounts);
        model.addAttribute("likeCounts", likeCounts);

        return "Snack/SnackOut";
    }
}
