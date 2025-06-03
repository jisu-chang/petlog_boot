package com.example.PetLog.User;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserDTO {

    int userId;
    String userLoginId;
    String password;
    String name;
    String phone;
    String email;
    MultipartFile profileimg;
    String rank;
    String userRole;
    int grapeCount;

}
