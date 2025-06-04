package com.example.PetLog.Pet;

import com.example.PetLog.User.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.catalina.User;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class PetDTO {

    long petId;
    String petName;
    String petBog;
    LocalDate petHbd;

    //UserDTO꺼
    Long userId;

    MultipartFile petImg;
    String petImgName; //이미지 파일명만 저장하기용
    String petNeuter;

    public PetEntity entity(UserEntity user) {

        //UserEntity user = new UserEntity();
        //user.setUserId((long) userId);

        return PetEntity.builder()
                .petId(petId)
                .petName(petName)
                .petBog(petBog)
                .petHbd(petHbd)
                .user(user)
                .petImg(petImgName)
                .petNeuter(petNeuter)
                .build();
    }
}
