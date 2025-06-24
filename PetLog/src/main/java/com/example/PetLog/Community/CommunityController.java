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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    String path ="C:\\MBC12AI\\git\\petlog_boot\\src\\main\\resources\\static\\image";

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

        // 댓글수 / 좋아요수 매핑
        Map<Long, Integer> commentCounts = new HashMap<>();
        Map<Long, Integer> likeCounts = new HashMap<>();

        for (CommunityEntity post : normalPosts) {
            Long id = post.getPostId();
            commentCounts.put(id, commentsService.getCommentsByPostId(id).size());
            likeCounts.put(id, likesService.getLikeCount(id));
        }

        mo.addAttribute("normalPosts", normalPosts);
        mo.addAttribute("noticePosts", noticePosts);
        mo.addAttribute("commentCounts", commentCounts);
        mo.addAttribute("likeCounts", likeCounts);
        mo.addAttribute("list", filtered);
        return "Community/CommunityOut";
    }

    // 커뮤니티 게시글 상세보기
    @GetMapping(value = "/CommunityDetail")
    public String comdetail(@RequestParam("postId") Long postId, Model mo, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        String userLoginId = (String) session.getAttribute("user_login_id");
        String userRole = (String) session.getAttribute("userRole");

        // 좋아요 처리
        CommunityEntity dto = communityService.findById(postId); // 게시글 정보 가져오기
        int likeCount = likesService.getLikeCount(postId); // 게시글의 좋아요 수 가져오기
        boolean likedByUser = likesService.islikedByUser(postId, userId, userLoginId); // 사용자가 해당 게시글에 좋아요 눌렀는지 확인

        // 댓글 목록 가져오기
        List<CommentsDTO> comments = commentsService.getCommentsByPostId(postId);

        communityService.readup(postId);

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
    public String comupdate(@RequestParam("postId") Long postId, Model mo) {
        CommunityEntity edto = communityService.updateById(postId);
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

    @GetMapping(value = "/CommunityDelete")
    public String comdelete(@RequestParam("postId") Long postId, Model mo) {
        CommunityEntity edto = communityService.deleteById(postId);
        mo.addAttribute("dto", edto);
        return "Community/CommunityDelete";
    }

    @PostMapping(value = "/CommunityDeleteSave")
    public String comdeletesave(@RequestParam("postId") Long postId,
                                @RequestParam("dfname") String dfname,
                                @RequestParam(value = "type", required = false) String type,
                                @RequestParam(value = "post_type", required = false) String postType,
                                HttpSession session,
                                CommunityDTO communityDTO) throws IOException {
        Long userId = (Long) session.getAttribute("userId");
        communityDTO.setUserId(userId);

        communityService.deletesave(postId);
        File ff = new File(path + "\\" + dfname);
        ff.delete();

        return "redirect:/CommunityOut";
    }

    // 공지사항 출력
    @GetMapping(value = "/CommunityNotice")
    public String NoticeView(Model mo) {
        List<CommunityEntity> noticePosts = communityService.getNoticePost();
        mo.addAttribute("noticePosts", noticePosts);
        return "Notice/NoticeOut";
    }

    //공지사항 게시글 상세보기
    @GetMapping(value = "/NoticeDetail")
    public String noticedetail(@RequestParam("postId") Long postId, Model mo, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        String userLoginId = (String) session.getAttribute("user_login_id");
        String userRole = (String) session.getAttribute("userRole");

        CommunityEntity dto = communityService.findById(postId); // 게시글 정보 가져오기

        communityService.readup(postId);

        mo.addAttribute("sessionUserId", userId);
        mo.addAttribute("sessionUserLoginId", userLoginId);
        mo.addAttribute("sessionUserRole", userRole);
        mo.addAttribute("dto", dto);
        return "Notice/NoticeDetail";
    }

    @GetMapping(value = "/NoticeUpdate")
    public String noticeupdate(@RequestParam("postId") Long postId, Model mo) {
        CommunityEntity edto = communityService.updateById(postId);
        mo.addAttribute("dto", edto);
        return "Notice/NoticeUpdate";
    }

    @PostMapping(value = "/NoticeUpdateSave")
    public String noticeupdatesave(@RequestParam("dfname") String dfname, @RequestParam("post_readcnt") int readcnt, CommunityDTO communityDTO, HttpSession session) throws IOException {
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

        return "redirect:/CommunityNotice";
    }

    @GetMapping(value = "/NoticeDelete")
    public String noticedelete(@RequestParam("postId") Long postId, Model mo) {
        CommunityEntity edto = communityService.deleteById(postId);
        mo.addAttribute("dto", edto);
        return "Notice/NoticeDelete";
    }

    @PostMapping(value = "/NoticeDeleteSave")
    public String noticedeletesave(@RequestParam("postId") Long postId,
                                @RequestParam("dfname") String dfname,
                                @RequestParam(value = "type", required = false) String type,
                                @RequestParam(value = "post_type", required = false) String postType,
                                HttpSession session,
                                CommunityDTO communityDTO) throws IOException {
        Long userId = (Long) session.getAttribute("userId");
        communityDTO.setUserId(userId);

        communityService.deletesave(postId);
        File ff = new File(path + "\\" + dfname);
        ff.delete();

        return "redirect:/CommunityNotice";
    }

    //댓글
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

        return "redirect:/CommunityDetail?postId=" + postId;
    }

    //좋아요
    @PostMapping("/post/{postId}/like")
    public String likeOnPost(@PathVariable Long postId, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        String userLoginId = (String) session.getAttribute("userLoginId");

        if (userId == null || userLoginId == null) {
            return "redirect:/login";
        }
        likesService.likeOnUser(postId, userId, userLoginId);

        return "redirect:/CommunityDetail?postId=" + postId;
    }

    @PostMapping("/community/comment")
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

        // 값이 null이 아닌 경우에만 파라미터 추가
        if (commentsDTO.getPost_id() != null) {
            redirectAttributes.addAttribute("postId", commentsDTO.getPost_id());
            return "redirect:/CommunityDetail"; // postId는 리다이렉트 속성으로 붙음
        } else {
            return "redirect:/Community";
        }
    }

}
