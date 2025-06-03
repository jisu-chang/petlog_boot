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
    //int userId;

    MultipartFile petImg;
    String petNeuter;

    public PetEntity entity() {
        String filename = (petImg != null) ? petImg.getOriginalFilename() : null;

        //UserEntity user = new UserEntity();
        //user.setUserId((long) userId);

        return PetEntity.builder()
                .petId(petId)
                .petName(petName)
                .petBog(petBog)
                .petHbd(petHbd)
                //.user(user)
                .petImg(filename)
                .petNeuter(petNeuter)
                .build();
    }
}
