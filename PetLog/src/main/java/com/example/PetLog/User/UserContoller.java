package com.example.PetLog.User;

import com.example.PetLog.Comments.CommentsEntity;
import com.example.PetLog.Comments.CommentsService;
import com.example.PetLog.Community.CommunityEntity;
import com.example.PetLog.Community.CommunityService;
import com.example.PetLog.Diary.DiaryEntity;
import com.example.PetLog.Diary.DiaryService;
import com.example.PetLog.Likes.LikesEntity;
import com.example.PetLog.Likes.LikesService;
import com.example.PetLog.Snack.SnackEntity;
import com.example.PetLog.Snack.SnackService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.graphql.GraphQlProperties;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.parameters.P;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Controller
public class UserContoller {

    @Autowired
    UserService userService;
    @Autowired
    UserRepository userRepository;
    @Autowired
    CommunityService communityService;
    @Autowired
    DiaryService diaryService;
    @Autowired
    SnackService snackService;
    @Autowired
    CommentsService commentsService;
    @Autowired
    LikesService likesService;

    String path = new File("src/main/resources/static/image").getAbsolutePath();

    //로그인
    @GetMapping(value = "/login")
    public String loginpage(){
        return "User/UserLogin";
    }

    //로그인 처리
    @PostMapping("/login")
    public String login(@RequestParam("userId") Long userId, HttpSession session) {
        // 로그인 성공 시 세션에 user_id 저장
        session.setAttribute("user_id", userId);
        return "redirect:/"; // 로그인 후 홈 페이지로 리디렉션
    }

    //회원가입
    @GetMapping("/signUp")
    public String signUp(Model model){
        model.addAttribute("userDTO",new UserDTO());
        return "User/UserSignUp";
    }

    @PostMapping("/signUpSave")
    public String member2(@ModelAttribute("userDTO") @Valid UserDTO userDTO, BindingResult bindingResult,
                          @RequestParam("idCheckStatus") String idCheckStatus, MultipartHttpServletRequest mul, Model mo) throws IOException {
        // 유효성 검사 에러 (ex. 이름, 이메일 등)
        if (bindingResult.hasErrors()) {
            return "User/UserSignUp";
        }

        // 비밀번호와 비밀번호 확인이 일치하는지 체크
        if (!userDTO.getPassword().equals(userDTO.getPasswordCheck())) {
            mo.addAttribute("passwordMismatch", "입력한 비밀번호가 일치하지 않습니다.");
            return "User/UserSignUp";
        }

        // 프로필 이미지 유무 확인
        MultipartFile mf = mul.getFile("profileimg");
        if (mf == null || mf.isEmpty()) {
            mo.addAttribute("error", "프로필 이미지를 다시 업로드해주세요.");
            return "User/UserSignUp";
        }
        // 아이디 중복검사 여부 확인
        if (!"true".equals(idCheckStatus)) {
            mo.addAttribute("idError", "아이디 중복 확인을 먼저 해주세요.");
            return "User/UserSignUp";
        }
        // 정상 로직 실행
        mf.transferTo(new File(path + "\\" + mf.getOriginalFilename()));
        userDTO.setProfileimgName(mf.getOriginalFilename());
        userService.signUpInsert(userDTO);
        return "redirect:/main";
    }

    //아이디 중복체크
    @GetMapping("/idCheck")
    @ResponseBody
    public String idcheck(@RequestParam("userLoginId") String userLoginId) {
        boolean isDuplicate = userService.idCheck(userLoginId);
        return isDuplicate ? "duplicate" : "ok";
    }

    //카카오 회원가입
    @GetMapping("/signUpKakao")
    public String signUpKakao(HttpSession session, Model model) {
        String email = (String) session.getAttribute("kakao_email");
        String name = (String) session.getAttribute("kakao_name");
        String profileImg = (String) session.getAttribute("kakao_profile");
//        String phone = (String) session.getAttribute("kakao_phone");

        // 세션에서 꺼낸 값을 모델에 다시 담기
        model.addAttribute("kakao_name", name);
        model.addAttribute("kakao_profile", profileImg);
        model.addAttribute("kakao_email", email);
//        model.addAttribute("kakao_phone", phone);
        return "/User/signUpKakao";
    }

    //카카오 회원가입 처리
    @PostMapping("/signUpKakaoSave")
    public String signUpKakaoSave(@ModelAttribute UserDTO dto, HttpSession session) {
        // 카카오에서 받아온 이미지 URL을 직접 저장
        String kakaoImageUrl = dto.getProfileimgName();  // 또는 dto.getProfileimg()로 받아온 경우
        dto.setProfileimg(null); // MultipartFile 안 씀
        dto.setProfileimgName(kakaoImageUrl); // 여기에 URL 문자열 직접 세팅

        dto.setPassword("kakao"); // 의미 없는 비번
        dto.setUser_role("USER");
        dto.setRank("일반회원");
        dto.setGrape_count(0);

        userService.save(dto);
        session.setAttribute("user_id", dto.getUser_id());
        session.setAttribute("user_login_id", dto.getUser_login_id());
        return "redirect:/"; // 회원가입 후 홈으로
    }

    //마이페이지
    @GetMapping("/MyPage")
    public String MyPage(Model mo, HttpSession session, Model model) {
        // 세션에서 로그인한 사용자 ID 가져오기
        Long userId = (Long) session.getAttribute("user_id");

        // 로그인되지 않은 경우 로그인 페이지로 리디렉트
        if (userId == null) {
            return "redirect:/login";
        }

        Optional<UserEntity> user = userRepository.findById(userId);
        if (user.isPresent()) {
            model.addAttribute("user", user.get());
        } else {

        }

        // UserService를 사용하여 userId로 UserEntity 객체 가져오기
        UserEntity userEntity = userService.findById(userId);  // userDTO가 아닌 userId 사용
        log.info("프로필 이미지: " + userEntity.getProfileimg());
        // UserEntity 객체를 모델에 추가하여 뷰에 전달
        mo.addAttribute("list", userEntity);
        return "User/UserMyPage";
    }

    //회원정보 수정
    @GetMapping("/UserUpdate")
    public String userUpdate(HttpSession session, Model mo){
        Long userId = (Long) session.getAttribute("user_id");
        UserEntity udto = userService.updateById(userId);
        mo.addAttribute("dto", udto);
        return "User/UserUpdate";
    }

    //아이디 찾기
    @GetMapping("/findId")
    public String findid() {
        return "User/UserFindId";
    }

    //아이디 찾기 처리
    @PostMapping("/findIdSave")
    public String findidsave(@RequestParam String name, @RequestParam String email, Model mo){
        String loginId=userService.findLoginIdByNameAndEmail(name, email);

        if(loginId != null){
            mo.addAttribute("foundId", loginId);
            return "User/UserFindIdResult";
        } else {
            mo.addAttribute("error", "일치하는 정보가 없습니다.");
            return "User/UserFindId";
        }
    }

    // 비밀번호 찾기
    @GetMapping("/findPw")
    public String findpw() {
        return "User/UserFindPw";
    }

    //비밀번호 찾기 처리
    @PostMapping("/findPwSave")
    public String findPw(@RequestParam String name,
                         @RequestParam String phone,
                         @RequestParam String userLoginId,
                         @RequestParam String email,
                         RedirectAttributes redirectAttributes,
                         Model mo) {

        boolean result = userService.processPasswordReset(name, userLoginId, email, phone);
        if (result) {
            return "redirect:/login?msg=sent";
        } else {
            mo.addAttribute("error", "입력한 정보와 일치하는 회원이 없습니다.");
            return "User/UserFindPw";
        }
    }

    //비밀번호 변경
    @GetMapping("/UserPwChange")
    public String changePwForm() {
        return "User/UserPwChange";
    }

    //비밀번호 변경 처리
    @PostMapping("/changePw")
    public String changePasswrod( @RequestParam String currentPw, @RequestParam String newPw, @RequestParam String newPwConfirm,
                                  HttpSession session,RedirectAttributes redirectAttributes) {
        Long userId = (Long) session.getAttribute("user_id");
        if (userId == null) {
            return "redirect:/login";
        }

        String result = userService.changePw(userId, currentPw, newPw, newPwConfirm);

        switch (result) {
            case "success" -> redirectAttributes.addFlashAttribute("msg", "비밀번호가 성공적으로 변경되었습니다.");
            case "wrong_current" -> redirectAttributes.addFlashAttribute("error", "현재 비밀번호가 틀립니다.");
            case "mismatch_confirm" -> redirectAttributes.addFlashAttribute("error", "새 비밀번호와 확인 비밀번호가 일치하지 않습니다.");
            case "same_as_old" -> redirectAttributes.addFlashAttribute("error", "새 비밀번호가 기존 비밀번호와 같습니다.");
            case "not_found" -> redirectAttributes.addFlashAttribute("error", "사용자 정보를 찾을 수 없습니다.");
        }
        return result.equals("success") ? "redirect:/MyPage" : "redirect:/UserPwChange";
    }

    //회원정보 수정 처리
    @PostMapping("/UserUpdateSave")
    public String userUpdateSave(UserDTO userDTO,@RequestParam("profileimg") MultipartFile mf, @RequestParam("dfname") String dfname, HttpSession session, Model mo) throws IOException {
        Long userId = (Long) session.getAttribute("user_id");
        //기존 유저 정보 가져오기
        UserEntity userEntity = userService.findById(userId);
        //기존 비밀번호 유지
        userDTO.setPassword(userEntity.getPassword());

        new File(path+"\\"+dfname).delete();
        mf.transferTo(new File(path+"\\"+mf.getOriginalFilename()));

        userDTO.setUser_id(userId);
        userService.updatesave(userDTO.toEntity());
        return "redirect:/MyPage";
    }

    //회원탈퇴- 활동이력 보이기
    @GetMapping("/UserDelete")
    public String userdelete(HttpSession session, Model mo){
        Long userId = (Long) session.getAttribute("user_id");
        if (userId == null) {
            return "redirect:/login";
        }

        UserEntity userEntity = userService.findById(userId);
        List<CommunityEntity> myPost = communityService.findByUserId(userId); // 게시글
        List<DiaryEntity> myDiary = diaryService.findByDiaryUserId(userId); // 일기
        List<SnackEntity> mySnack = snackService.findByUserId(userId); // 간식
        List<CommentsEntity> myComment = commentsService.findByUserId(userId); // 댓글
        List<LikesEntity> myLike = likesService.findByUserId(userId); // 좋아요

        mo.addAttribute("user", userEntity);
        mo.addAttribute("myPost", myPost);
        mo.addAttribute("mySnack", mySnack);
        mo.addAttribute("myDiary", myDiary);
        mo.addAttribute("myComment", myComment);
        mo.addAttribute("myLike", myLike);
        return "User/UserDelete";
    }

    //회원탈퇴 처리
    @PostMapping("/user/withdraw")
    public String withdraw(HttpSession session) {
        Long userId = (Long) session.getAttribute("user_id");
        if (userId == null) return "redirect:/login";

        likesService.deleteByUserId(userId); //좋아요 삭제
        commentsService.deleteByUserId(userId); //댓글 삭제
        communityService.deleteByUserId(userId); //게시글 삭제
        diaryService.deleteByUserId(userId); //일기 삭제
        snackService.deleteByUserId(userId); //간식 삭제
        userService.deleteUser(userId);  //유저 삭제

        session.invalidate(); // 세션 종료
        return "redirect:/DeleteMessage";
    }
}
