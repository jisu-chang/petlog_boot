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
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Controller
public class UserController {

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

    String path ="C:\\MBC12AI\\git\\petlog_boot\\PetLog\\src\\main\\resources\\static\\image";

    //로그인
    @GetMapping(value = "/login")
    public String loginpage(){
        return "User/UserLogin";
    }

    //로그인 처리
    @PostMapping("/login")
    public String login(@RequestParam("userLoginId") String userLoginId,
                        @RequestParam("password") String password,
                        HttpSession session,
                        Model model) {

        // 서비스에서 아이디와 비밀번호로 사용자 조회 및 인증 처리
        UserEntity user = userService.login(userLoginId, password);

        if (user != null) {
            // 로그인 성공 시 세션에 userId 저장
            session.setAttribute("userId", user.getUserId());
            session.setAttribute("userLoginId", user.getUserLoginId());
            session.setAttribute("userRole", user.getUserRole());
            session.setAttribute("name", user.getName());
            session.setAttribute("grapeCount", user.getGrapeCount());
            session.setAttribute("rank", user.getRank());
            session.setAttribute("profileimg", user.getProfileimg());
            // 추가 세션 정보 필요하면 넣기

            return "redirect:/";  // 홈 페이지로 이동
        } else {
            // 로그인 실패 시 에러 메시지 모델에 담아 로그인 페이지로 이동
            model.addAttribute("loginError", "아이디 또는 비밀번호가 올바르지 않습니다.");
            return "User/UserLogin";
        }
    }

    @RequestMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        // 세션을 무효화
        request.getSession().invalidate();
        return "redirect:/login";  // 로그인 페이지로 리다이렉트
    }

    //회원가입
    @GetMapping("/signUp")
    public String signUp(Model model){
        model.addAttribute("userDTO",new UserDTO());
        return "User/UserSignUp";
    }

    //회원가입 처리
    @PostMapping("/signUpSave")
    public String member2(@ModelAttribute("userDTO") @Valid UserDTO userDTO, BindingResult bindingResult, @RequestParam("phoneCheckStatus") String phoneCheckStatus,
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

        // 아이디 중복검사 여부 확인
        if (!"true".equals(idCheckStatus)) {
            mo.addAttribute("idError", "아이디 중복 확인을 먼저 해주세요.");
            return "User/UserSignUp";
        }

        // 프로필 이미지 유무 확인
        MultipartFile mf = mul.getFile("profileimg");
        if (mf == null || mf.isEmpty()) {
            mo.addAttribute("error", "프로필 이미지를 다시 업로드해주세요.");
            return "User/UserSignUp";
        }

        // UUID 생성 (파일 이름 고유화)
        String ff = mf.getOriginalFilename(); // 원본 파일 이름
        String fname = ff != null ? ff.substring(ff.lastIndexOf(".")) : "";  // 파일 확장자 추출
        String nfname = UUID.randomUUID().toString() + fname;  // UUID로 고유한 파일명 만들기

        File uploadDirectory = new File(path);
        if (!uploadDirectory.exists()) {
            uploadDirectory.mkdirs(); // 디렉토리가 없으면 생성 (상위 디렉토리도 함께 생성)
        }

        try {
            mf.transferTo(new File(path + File.separator + nfname));
        } catch (IOException e) {
            // 이미지 저장 실패 시 사용자에게 오류 메시지 전달
            mo.addAttribute("error", "프로필 이미지 저장 중 오류가 발생했습니다.");

            return "User/UserSignUp";
        }

        // 새로운 파일명을 DTO에 설정
        userDTO.setProfileimgName(nfname);
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
    public String signUpKakaoSave(@ModelAttribute UserDTO dto, HttpSession session, Model model) {
        // 세션에서 카카오 정보 꺼내기
        String email = (String) session.getAttribute("kakao_email");
        String name = (String) session.getAttribute("kakao_name");
        String profile = (String) session.getAttribute("kakao_profile");

        dto.setEmail(email);
        dto.setName(name);
        dto.setProfileimgName(profile);

        // 이메일 중복 검사
        Optional<UserEntity> existingUser = userRepository.findByEmail(email);
        if (existingUser.isPresent()) {
            UserEntity user = existingUser.get();
            session.setAttribute("userId", user.getUserId());
            session.setAttribute("userLoginId", user.getUserLoginId());
            return "redirect:/";
        }

        // 아이디 중복 검사
        if (userRepository.existsByUserLoginIdNative(dto.getUserLoginId()) > 0) {
            model.addAttribute("error", "이미 사용 중인 아이디입니다.");
            return "/User/signUpKakao";
        }

        dto.setPassword("kakao");
        dto.setUserRole("USER");
        dto.setRank("일반회원");
        dto.setGrapeCount(0);

        UserEntity savedUser = userRepository.save(dto.toEntity());
        session.setAttribute("userId", savedUser.getUserId());
        session.setAttribute("userLoginId", savedUser.getUserLoginId());
        return "redirect:/";
    }

    //아이디 찾기
    @GetMapping("/findId")
    public String findid(Model mo) {
        mo.addAttribute("userFindIdDTO", new UserFindIdDTO());
        return "User/UserFindId";
    }

    //아이디 찾기 처리
    @PostMapping("/findIdSave")
    public String findidsave(@Valid @ModelAttribute UserFindIdDTO dto, BindingResult bindingResult, Model mo) {

        // 유효성 검사
        if (bindingResult.hasErrors()) {
            return "User/UserFindId"; // 다시 아이디 찾기 화면으로
        }

        // 이름, 이메일, 전화번호로 아이디를 찾는 로직
        String userId = userService.findLoginIdByInfo(dto.getName(), dto.getEmail(), dto.getPhone());

        if (userId != null) {
            mo.addAttribute("userId", userId);
            return "User/UserFindIdResult"; // 아이디 찾기 결과 화면으로 리다이렉트
        } else {
            mo.addAttribute("error", "입력한 정보와 일치하는 회원이 없습니다.");
            return "User/UserFindId"; // 에러 메시지 출력 후 다시 아이디 찾기 화면
        }
    }

    // 비밀번호 찾기
    @GetMapping("/findPw")
    public String findpw() {
        return "User/UserFindPw";
    }

    //비밀번호 찾기 처리
    @PostMapping("/findPwSave")
    public String findPw(@Valid @ModelAttribute UserFindPwDTO dto, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return "User/UserFindPw";
        }

        boolean result = userService.processPasswordReset(dto.getName(), dto.getUserLoginId(), dto.getEmail(), dto.getPhone());
        if (result) {
            return "redirect:/login?msg=sent";
        } else {
            model.addAttribute("error", "입력한 정보와 일치하는 회원이 없습니다.");
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
    public String changePw(@ModelAttribute UserPasswordUpdateDTO dto, BindingResult bindingResult,
                           Model model, HttpSession session) {

        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            model.addAttribute("error", "로그인이 필요합니다.");
            return "User/UserLogin"; // 로그인 페이지로
        }

        // 유효성 검사 오류가 있으면 다시 폼으로 .. BindingResult -> 유효성 검사 결과
        if (bindingResult.hasErrors()) {
            return "User/UserPwChange";
        }

        // 현재 비밀번호 확인
        boolean success = userService.changePassword(userId, dto.getCurrentPw(), dto.getNewPw(), dto.getNewPwConfirm());

        if (success) {
            model.addAttribute("success", "비밀번호가 성공적으로 변경되었습니다.");
        } else {
            model.addAttribute("error", "비밀번호 변경에 실패했습니다. 입력값을 확인해주세요.");
        }

        return "redirect:/MyPage";
    }

    @GetMapping("/MyPage")
    public String MyPage(Model mo, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");

        if (userId == null) {
            System.out.println("세션에 userId가 없습니다. 로그인 페이지로 리디렉션됩니다.");
            return "redirect:/login";
        }

        UserDTO userProfile = userService.getUserProfileWithEquippedFrame(userId);

        if (userProfile == null) {
            mo.addAttribute("error", "유저 정보를 찾을 수 없습니다.");
            return "User/UserError";
        }

        long timestamp = System.currentTimeMillis();
        mo.addAttribute("timestamp", timestamp);
        mo.addAttribute("list", userProfile);
        return "User/UserMyPage";
    }

    //회원정보 수정
    @GetMapping("/UserUpdate")
    public String userUpdate(Model model, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");

        // userId로 사용자 정보를 조회
        UserDTO userDTO = userService.getUserDTOById(userId); // 사용자 정보 가져오기

        model.addAttribute("userDTO", userDTO);  // userDTO를 모델에 담아 뷰로 전달
        return "User/UserUpdate";  // 수정 페이지로 이동
    }

    //회원정보 수정 처리
    @PostMapping("/UserUpdateSave")
    public String userUpdateSave(@Valid @ModelAttribute UserUpdateDTO dto, BindingResult bindingResult,
                                 @RequestParam("dfname") String dfname, HttpSession session, Model model) throws IOException {

        if (bindingResult.hasErrors()) {
            System.out.println("⚠ 유효성 검사 실패:");
            bindingResult.getAllErrors().forEach(error -> {
                System.out.println(" - " + error.getDefaultMessage());
            });
            return "User/UserUpdate";
        }

        // 기존 유저 정보 가져오기
        Long userId = (Long) session.getAttribute("userId");
        UserEntity userEntity = userService.findById(userId);

        if (userEntity == null) {
            model.addAttribute("error", "사용자를 찾을 수 없습니다.");
            return "User/UserError";
        }

        String loginType = (String) session.getAttribute("loginType");

        // 카카오 유저면 이메일 수정 금지
        if ("kakao".equals(loginType)) {
            dto.setEmail(userEntity.getEmail()); // 기존 이메일로 덮어쓰기
        }

        // 기존 비밀번호 유지
        String encryptedPassword = userEntity.getPassword();

        // 프로필 이미지 처리
        MultipartFile mf = dto.getProfileimg();
        String fname;

        if (mf != null && !mf.isEmpty()) {
            // 기존 이미지 삭제 (기본 이미지는 제외)
            if (!"default.png".equals(dfname)) {
                File oldFile = new File(path + "\\" + dfname);
                if (oldFile.exists()) oldFile.delete();
            }

            // UUID + 확장자 처리
            String originalFilename = mf.getOriginalFilename();
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            fname = UUID.randomUUID().toString() + extension;

            // 저장
            mf.transferTo(new File(path + "\\" + fname));
        } else {
            fname = dfname; // 새 이미지 없으면 기존 이미지 유지
        }

        // DB 및 세션 정보 업데이트
        userEntity.setUserLoginId(dto.getUserLoginId());
        userEntity.setProfileimg(dto.getProfileimgName());
        userEntity.setName(dto.getName());
        userEntity.setPhone(dto.getPhone());
        userEntity.setEmail(dto.getEmail());
        userEntity.setProfileimg(fname);
        userEntity.setPassword(encryptedPassword);

        try {
            userService.updateUser(userEntity);
        } catch (Exception e) {
            e.printStackTrace();
        }

        session.setAttribute("userId", userEntity.getUserId());
        session.setAttribute("userLoginId", userEntity.getUserLoginId());
        session.setAttribute("userRole", userEntity.getUserRole());
        session.setAttribute("name", userEntity.getName());
        session.setAttribute("grapeCount", userEntity.getGrapeCount());
        session.setAttribute("rank", userEntity.getRank());
        session.setAttribute("profileimgName", fname);

        return "redirect:/MyPage";  // 수정 후 마이페이지로 리디렉션
    }

    //회원탈퇴- 활동이력 보이기
    @GetMapping("/UserDelete")
    public String userdelete(HttpSession session, Model mo){
        Long userId = (Long) session.getAttribute("userId");
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
        Long userId = (Long) session.getAttribute("userId");

        likesService.deleteByUserId(userId); //좋아요 삭제
        commentsService.deleteByUserId(userId); //댓글 삭제
        communityService.deleteByUserId(userId); //게시글 삭제
        diaryService.deleteByUserId(userId); //일기 삭제
        snackService.deleteByUserId(userId); //간식 삭제
        userService.deleteUser(userId);  //유저 삭제

        session.invalidate(); // 세션 종료
        return "User/DeleteMessage";
    }
}
