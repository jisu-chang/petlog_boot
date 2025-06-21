package com.example.PetLog.Community;

import com.example.PetLog.Comments.CommentsDTO;
import com.example.PetLog.Comments.CommentsService;
import com.example.PetLog.Likes.LikesService;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Controller
public class CommunityController {

    @Autowired
    CommunityService communityService;
    @Autowired
    LikesService likesService;
    @Autowired
    CommentsService commentsService;

    String path = new File("src/main/resources/static/image").getAbsolutePath();

    // 커뮤니티/공지사항 폼을 type에 따라 다르게 로딩
    @GetMapping("/CommunityIn")
    public String comin(@RequestParam(value = "type", defaultValue = "normal") String type, Model mo) {
        mo.addAttribute("postType", type);
        if ("notice".equals(type)) {
            return "Notice/NoticeInput"; // 공지사항이면 Notice 폴더
        } else {
            return "Community/CommunityInput"; // 일반글이면 Community 폴더
        }
    }

    // 게시글 저장
    @PostMapping(value = "/CommunityInSave")
    public String cominsave(@ModelAttribute CommunityDTO communityDTO, @RequestParam("postType") String postType,HttpSession session) throws IOException {
        // 로그인 한 유저 정보 가져옴
        String userRole = (String) session.getAttribute("userRole");
        Long userId = (Long) session.getAttribute("userId");

        // DTO 객체에 데이터 설정
        communityDTO.setUserId(userId);
        communityDTO.setPostDate(LocalDate.now()); // 오늘 날짜
        communityDTO.setPostReadcnt(0); // 조회수 기본 0
        communityDTO.setPostType(postType);

        if ("notice".equals(communityDTO.getPostType())) {
            if (userRole == null || !"admin".equalsIgnoreCase(userRole)) {
                communityDTO.setPostType("normal");
            }
        }

        // 이미지 처리
        MultipartFile mf = communityDTO.getPostImage();
        if (mf != null && !mf.isEmpty()) {
            String fname = mf.getOriginalFilename();
            mf.transferTo(new File(path + "\\" + fname));
        } else {
            communityDTO.setPostImage(null);
        }
        CommunityEntity communityEntity = communityDTO.entity();
        communityService.insertpost(communityEntity);

        if ("notice".equals(communityDTO.getPostType())) {
            return "redirect:/CommunityNotice";
        } else {
            return "redirect:/CommunityOut";
        }
    }

    // 커뮤니티 게시글 목록
    @GetMapping(value = "/CommunityOut")
    public String comout(Model mo) {
        List<CommunityEntity> list = communityService.allout();
        // 탈퇴회원 글 제외
        List<CommunityEntity> filtered = list.stream()
                .filter(cc -> cc.getUser() != null)
                .collect(Collectors.toList());

        // 공지사항 출력
        List<CommunityEntity> noticePosts = list.stream()
                .filter(post -> "notice".equals(post.getPostType()))
                .collect(Collectors.toList());

        // 일반글 출력
        List<CommunityEntity> normalPosts = list.stream()
                .filter(post -> "normal".equals(post.getPostType()))
                .collect(Collectors.toList());

        mo.addAttribute("list", filtered);
        mo.addAttribute("noticePosts", noticePosts);
        mo.addAttribute("normalPosts", normalPosts);
        return "Community/CommunityOut";
    }

    // 공지사항 출력
    @GetMapping(value = "/CommunityNotice")
    public String NoticeView(Model mo) {
        List<CommunityEntity> noticePosts = communityService.getNoticePost();
        mo.addAttribute("noticePosts", noticePosts);
        return "Notice/NoticeOut";
    }

    // 커뮤니티 게시글 상세보기
    @GetMapping(value = "/CommunityDetail")
    public String comdetail(@RequestParam("num") Long num, Model mo, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        String userLoginId = (String) session.getAttribute("user_login_id");
        String userRole = (String) session.getAttribute("userRole");

        // 좋아요 처리
        CommunityEntity dto = communityService.findById(num); // 게시글 정보 가져오기
        int likeCount = likesService.getLikeCount(num); // 게시글의 좋아요 수 가져오기
        boolean likedByUser = likesService.islikedByUser(num, userId, userLoginId); // 사용자가 해당 게시글에 좋아요 눌렀는지 확인

        // 댓글 목록 가져오기
        List<CommentsDTO> comments = commentsService.getCommentsByPostId(num);

        communityService.readup(num);

        mo.addAttribute("sessionUserId", userId);
        mo.addAttribute("sessionUserLoginId", userLoginId);
        mo.addAttribute("sessionUserRole", userRole);
        mo.addAttribute("dto", dto);
        mo.addAttribute("likeCount", likeCount); // 좋아요 수
        mo.addAttribute("likedByUser", likedByUser); // 사용자가 좋아요를 눌렀는지 여부
        mo.addAttribute("comments", comments); // 댓글 추가
        return "Community/CommunityDetail";
    }

    @GetMapping(value = "/CommunityUpdate")
    public String comupdate(@RequestParam("unum") Long unum, Model mo) {
        CommunityEntity edto = communityService.updateById(unum);
        mo.addAttribute("dto", edto);
        return "Community/CommunityUpdate";
    }

    @PostMapping(value = "/CommunityUpdateSave")
    public String comupdatesave(@RequestParam("dfname") String dfname, @RequestParam("post_readcnt") int readcnt, CommunityDTO communityDTO, HttpSession session) throws IOException {
        Long userId = (Long) session.getAttribute("userId");
        communityDTO.setUserId(userId);
        communityDTO.setPostReadcnt(readcnt);

        MultipartFile mf = communityDTO.getPostImage();
        String saveImageName;
        if (mf != null && !mf.isEmpty()) {
            new File(path + "\\" + dfname).delete(); // 기존 이미지 삭제
            saveImageName = mf.getOriginalFilename();
            mf.transferTo(new File(path + "\\" + saveImageName));
        } else {
            saveImageName = dfname; // 새 이미지 없으면 기존 이미지 유지
        }

        CommunityEntity communityEntity = CommunityEntity.builder()
                .postId(communityDTO.getPostId())
                .userId(communityDTO.getUserId())
                .postTitle(communityDTO.getPostTitle())
                .postContent(communityDTO.getPostContent())
                .postImage(saveImageName)
                .postReadcnt(communityDTO.getPostReadcnt())
                .postDate(communityDTO.getPostDate())
                .postType(communityDTO.getPostType())
                .build();
        communityService.insertpost(communityEntity);

        return "redirect:/CommunityOut";
    }

    @PostMapping(value = "/CommunityDelete")
    public String comdelete(@RequestParam("dnum") Long dnum, Model mo) {
        CommunityEntity edto = communityService.deleteById(dnum);
        mo.addAttribute("dto", edto);
        return "Community/CommunityDelete";
    }

    @PostMapping(value = "/CommunityDeleteSave")
    public String comdeletesave(@RequestParam("dnum") Long dnum, @RequestParam("dfname") String dfname, HttpSession session, CommunityDTO communityDTO) throws IOException {
        Long userId = (Long) session.getAttribute("user_id");
        communityDTO.setUserId(userId);

        communityService.deletesave(dnum);
        File ff = new File(path + "\\" + dfname);
        ff.delete();
        return "redirect:/CommunityOut";
    }

    @GetMapping("/community/{postId}")
    public String viewPost(@PathVariable Long postId, HttpSession session, Model mo) {
        Long userId = (Long) session.getAttribute("userId");
        String userLoginId = (String) session.getAttribute("userLoginId");

        CommunityEntity post = communityService.getPostById(postId);

        boolean likedByUser = false;
        if (userId != null && userLoginId != null) {
            likedByUser = likesService.islikedByUser(postId, userId, userLoginId);
        }

        int likeCount = likesService.getLikeCount(postId);
        mo.addAttribute("dto", post);
        mo.addAttribute("likedByUser", likedByUser);
        mo.addAttribute("likeCount", likeCount);

        return "community/CommunityDetail";
    }

    @PostMapping("/post/{postId}/like")
    public String likeOnPost(@PathVariable Long postId, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        String userLoginId = (String) session.getAttribute("userLoginId");

        if (userId == null || userLoginId == null) {
            return "redirect:/login";
        }
        likesService.likeOnUser(postId, userId, userLoginId);

        return "redirect:/community/" + postId;
    }

//    @PostMapping("/community/comment")
//    public String savecomment(@ModelAttribute("commentsDTO") CommentsDTO commentsDTO, RedirectAttributes redirectAttributes) {
//        commentsService.saveComment(commentsDTO);
//        redirectAttributes.addAttribute("postId", commentsDTO.getPost_id());
//        return "redirect:/CommunityDetail?num=" + commentsDTO.getPost_id();
//    }

    @PostMapping("/community/comment")
    public String saveComment(@ModelAttribute("commentsDTO") CommentsDTO commentsDTO,
                              RedirectAttributes redirectAttributes) {

        commentsService.saveComment(commentsDTO);

        // 값이 null이 아닌 경우에만 파라미터 추가
        if (commentsDTO.getPost_id() != null) {
            redirectAttributes.addAttribute("num", commentsDTO.getPost_id());
        } else {
            // 예외 처리 또는 기본 페이지로 리다이렉트
            return "redirect:/Community"; // 또는 에러 페이지
        }

        return "redirect:/CommunityDetail";
    }

}
