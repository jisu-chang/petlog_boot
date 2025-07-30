package com.example.PetLog.Community;

import com.example.PetLog.Comments.CommentsDTO;
import com.example.PetLog.Comments.CommentsService;
import com.example.PetLog.Likes.LikesService;
import com.example.PetLog.User.UserService;
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
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

@Slf4j
@Controller
public class CommunityController {

    @Autowired
    CommunityService communityService;
    @Autowired
    LikesService likesService;
    @Autowired
    CommentsService commentsService;
    @Autowired
    UserService userService;

    String path = "C:/petlog-uploads/community";

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
    public String cominsave(@ModelAttribute CommunityDTO communityDTO, @RequestParam("postType") String postType,HttpSession session, RedirectAttributes redirectAttributes) throws IOException {
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

        // 업로드 폴더 없으면 자동 생성
        File uploadDir = new File(path);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        // 이미지 처리
        MultipartFile mf = communityDTO.getPostImage();
        if (mf != null && !mf.isEmpty()) {
            // UUID 생성 (파일 이름 고유화)
            String ff = mf.getOriginalFilename(); // 원본 파일 이름
            String fname = ff != null ? ff.substring(ff.lastIndexOf(".")) : "";  // 파일 확장자 추출
            String nfname = UUID.randomUUID().toString() + fname;  // UUID로 고유한 파일명 만들기
            mf.transferTo(new File(path + "\\" + nfname)); //저장
            communityDTO.setPostImageName(nfname);
        } else {
            communityDTO.setPostImage(null);
        }
        CommunityEntity communityEntity = communityDTO.entity();
        communityService.insertpost(communityEntity);

        // --- 포도알 +1 로직 추가 시작 ---
        if (userId != null) {
            userService.addGrapeCount(userId, 1);
        }

        String redirectUrl;
        if ("notice".equals(communityDTO.getPostType())) {
            redirectUrl = "/CommunityNotice";
        } else {
            redirectUrl = "/CommunityOut";
            redirectAttributes.addAttribute("success", "grape"); // URL 파라미터로 'success=grape' 추가
        }

        if ("notice".equals(communityDTO.getPostType())) {
            return "redirect:/CommunityNotice";
        } else {
            return "redirect:/CommunityOut";
        }
    }

    // 커뮤니티 게시글 목록
    @GetMapping(value = "/CommunityOut")
    public String comout(Model mo,
                         @RequestParam(value = "page", defaultValue = "0") int page,
                         @RequestParam(value = "size", defaultValue = "5") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("postDate").descending());

        // 공지사항 페이징
        Page<CommunityEntity> noticePage = communityService.getPostsByType("notice", pageable);
        // 일반글 페이징
        Page<CommunityEntity> normalPage = communityService.getPostsByType("normal", pageable);

        // 댓글수 / 좋아요수 매핑 (normalPosts만)
        Map<Long, Integer> commentCounts = new HashMap<>();
        Map<Long, Integer> likeCounts = new HashMap<>();

        for (CommunityEntity post : normalPage.getContent()) {
            Long id = post.getPostId();
            commentCounts.put(id, commentsService.getCommentsByPostId(id).size());
            likeCounts.put(id, likesService.getLikeCount(id));
        }

        mo.addAttribute("noticePosts", noticePage);
        mo.addAttribute("normalPosts", normalPage);
        mo.addAttribute("commentCounts", commentCounts);
        mo.addAttribute("likeCounts", likeCounts);

        return "Community/CommunityOut";
    }

    // 커뮤니티 게시글 상세보기
    @GetMapping(value = "/CommunityDetail")
    public String comdetail(@RequestParam("postId") Long postId, Model mo, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        String userLoginId = (String) session.getAttribute("userLoginId");
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
        System.out.println("postImage: " + dto.getPostImage());
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

        // 저장 경로
        File uploadDir = new File(path);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        // 이미지 처리
        MultipartFile mf = communityDTO.getPostImage();
        String saveImageName;

        if (mf != null && !mf.isEmpty()) {
            // 기존 이미지 삭제 (기본 이미지가 아닌 경우만)
            if (dfname != null && !dfname.equals("default.png")) {
                File oldFile = new File(path + File.separator + dfname);
                if (oldFile.exists()) oldFile.delete();
            }

            // UUID + 확장자 생성
            String ff = mf.getOriginalFilename();
            String fname = ff.substring(ff.lastIndexOf("."));
            saveImageName = UUID.randomUUID().toString() + fname;

            // 저장
            mf.transferTo(new File(path + File.separator + saveImageName));
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
//                                @RequestParam("dfname") String dfname,
                                @RequestParam(value = "type", required = false) String type,
                                @RequestParam(value = "post_type", required = false) String postType,
                                HttpSession session,
                                CommunityDTO communityDTO) throws IOException {
        Long userId = (Long) session.getAttribute("userId");
        communityDTO.setUserId(userId);

        communityService.deletesave(postId);
//        File ff = new File(path + "\\" + dfname);
//        ff.delete();
        return "redirect:/CommunityOut";
    }

    @GetMapping("/search")
    public String search(@RequestParam String keyword, @RequestParam String postType, @RequestParam String board, Model model) {

        if (board.equals("notice")) {
            List<CommunityEntity> noticeResults = communityService.searchNotice(keyword, postType);
            model.addAttribute("noticePosts", noticeResults);
            return "Notice/NoticeOut"; // 공지사항 페이지로 이동
        } else {
            List<CommunityEntity> communityResults = communityService.searchCommunity(keyword, postType);

            // 댓글 수, 좋아요 수도 함께 가져오기
            Map<Long, Integer> commentCounts = communityService.getCommentCountsByList(communityResults);
            Map<Long, Integer> likeCounts = communityService.getLikeCountsByList(communityResults);

            // 이름으로 전달
            model.addAttribute("normalPosts", communityResults);
            model.addAttribute("commentCounts", commentCounts);
            model.addAttribute("likeCounts", likeCounts);

            return "Community/CommunityOut"; // 커뮤니티 페이지로 이동
        }
    }

    @GetMapping("/CommunityNotice")
    public String noticePaging(@RequestParam(defaultValue = "1") int page, Model model) {
        int pageSize = 5; // 한 페이지에 5개
        int startRow = (page - 1) * pageSize + 1;
        int endRow = page * pageSize;

        List<CommunityEntity> noticePosts = communityService.getNoticePostsPaging(startRow, endRow);
        int totalPosts = communityService.countNoticePosts();
        int totalPages = (int) Math.ceil((double) totalPosts / pageSize);

        model.addAttribute("noticePosts", noticePosts);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);

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

        // 저장 경로
        File uploadDir = new File(path);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        // 이미지 처리
        MultipartFile mf = communityDTO.getPostImage();
        String saveImageName;

        if (mf != null && !mf.isEmpty()) {
            // 기존 이미지 삭제 (기본 이미지가 아닌 경우만)
            if (dfname != null && !dfname.equals("default.png")) {
                File oldFile = new File(path + File.separator + dfname);
                if (oldFile.exists()) oldFile.delete();
            }

            // UUID + 확장자 생성
            String ff = mf.getOriginalFilename();
            String fname = ff.substring(ff.lastIndexOf("."));
            saveImageName = UUID.randomUUID().toString() + fname;

            // 저장
            mf.transferTo(new File(path + File.separator + saveImageName));
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
