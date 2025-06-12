package com.example.PetLog.Community;

import com.example.PetLog.Likes.LikesService;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
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

    String path = new File("src/main/resources/static/image").getAbsolutePath();

    @GetMapping(value = "/CommunityIn")
    public String comin(){
        return "Community/CommunityInput";
    }

    @PostMapping(value = "/CommunityInSave")
    public String cominsave(@ModelAttribute CommunityDTO communityDTO, HttpSession session) throws IOException {
        //로그인 한 유저 정보가져옴
        Long userId = (Long) session.getAttribute("userId");

        //DTO 객체에 데이터 설정
        communityDTO.setUserId(userId);
        communityDTO.setPostDate(LocalDate.now()); //오늘날짜
        communityDTO.setPostReadcnt(0); //조회수 기본 0
        communityDTO.setPostType("normal"); //게시글 작성 기본 normal

        MultipartFile mf = communityDTO.getPostImage();
        if(mf != null && !mf.isEmpty()){
            String fname=mf.getOriginalFilename();
            mf.transferTo(new File(path+"\\"+fname));
        } else{
            communityDTO.setPostImage(null);
        }
        CommunityEntity communityEntity = communityDTO.entity();
        communityService.insertpost(communityEntity);

        return "redirect:/CommunityOut";
    }

    @GetMapping(value = "/CommunityOut")
    public String comout(Model mo){
        List<CommunityEntity> list = communityService.allout();
        // 탈퇴회원 글 제외
        List<CommunityEntity> filtered = list.stream()
                .filter(cc -> cc.getUser() != null)
                .collect(Collectors.toList());

        mo.addAttribute("list", filtered);
        return "Community/CommunityOut";
    }

    //게시글 상세보기
    @GetMapping(value = "/CommunityDetail")
    public String comdetail(@RequestParam("num") Long num, Model mo, HttpSession session){
        // 세션에서 로그인한 사용자 정보 가져오기 (예: user_id, user_role)
        Long userId = (Long) session.getAttribute("userId");
        String userLoginId = (String) session.getAttribute("user_login_id");
        String userRole = (String) session.getAttribute("user_role");

        CommunityEntity dto = communityService.findById(num); //게시글 정보 가져오기
        int likeCount = likesService.getLikeCount(num); // 게시글의 좋아요 수 가져오기
        boolean likedByUser = likesService.islikedByUser(num,userId,userLoginId); //사용자가 해당 게시글에 좋아요 눌렀는지 확인

        communityService.readup(num);

    // 필요시 model에 같이 넘기기 (thymeleaf에서는 session에도 접근 가능하지만 안전하게 모델로도 넘길 수 있음)
        mo.addAttribute("sessionUserId", userId);
        mo.addAttribute("sessionUserRole", userLoginId);
        mo.addAttribute("dto", dto);
        mo.addAttribute("likeCount", likeCount);  // 좋아요 수
        mo.addAttribute("likedByUser", likedByUser);  // 사용자가 좋아요를 눌렀는지 여부
        return "Community/CommunityDetail";
    }

    @GetMapping(value = "/CommunityUpdate")
    public String comupdate(@RequestParam("unum") Long unum, Model mo){
        CommunityEntity edto = communityService.updateById(unum);
        mo.addAttribute("dto", edto);
        return "Community/CommunityUpdate";
    }

    @PostMapping(value = "/CommunityUpdateSave")
    public String comupdatesave(@RequestParam("dfname") String dfname, @RequestParam("post_readcnt") int readcnt, CommunityDTO communityDTO, HttpSession session) throws IOException {
        //로그인 한 유저 정보가져옴
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

        // Entity 생성 시 강제로 이미지 이름 넘기기
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
    public String comdelete(@RequestParam("dnum") Long dnum, Model mo){
        CommunityEntity edto = communityService.deleteById(dnum);
        mo.addAttribute("dto", edto);
        return "Community/CommunityDelete";
    }

    @PostMapping(value = "/CommunityDeleteSave")
    public String comdeletesave(@RequestParam("dnum") Long dnum, @RequestParam("dfname") String dfname, HttpSession session, CommunityDTO communityDTO) throws IOException {
        //로그인 한 유저 정보가져옴
        Long userId = (Long) session.getAttribute("user_id");
        communityDTO.setUserId(userId);

        communityService.deletesave(dnum);
        File ff=new File(path+"\\"+dfname);
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
}
