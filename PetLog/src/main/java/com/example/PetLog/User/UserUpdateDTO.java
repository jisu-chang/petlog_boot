package com.example.PetLog.User;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Data
public class UserUpdateDTO {
    private Long userId;

    @NotBlank(message = "이름은 필수입니다.")
    private String name;

    @NotBlank(message = "전화번호는 필수입니다.")
    private String phone;

    @NotBlank(message = "아이디는 필수입니다.")
    private String userLoginId;

    @Email(message = "이메일 형식이 올바르지 않습니다.")
    private String email;

    private MultipartFile profileimg;
    private String profileimgName;

    public String getProfileimgName() {
        return (profileimgName != null && !profileimgName.isEmpty()) ? profileimgName : "default.png";
    }

    public void handleProfileImage(MultipartFile profileimg) throws IOException {
        if (profileimg != null && !profileimg.isEmpty()) {
            // 파일명 생성 (UUID + 확장자)
            String originalFilename = profileimg.getOriginalFilename();
            String extension = originalFilename != null ? originalFilename.substring(originalFilename.lastIndexOf(".")) : "";
            profileimgName = UUID.randomUUID().toString() + extension;

            // 파일 저장 경로 (여기서는 로컬 서버 경로)
            String uploadPath = "C:/upload/image";
            File directory = new File(uploadPath);
            if (!directory.exists()) {
                directory.mkdirs();  // 폴더가 없으면 생성
            }
            String filePath = uploadPath + File.separator + profileimgName;

            // 파일을 지정된 경로에 저장
            profileimg.transferTo(new File(filePath));  // 파일 저장
        }
    }

}