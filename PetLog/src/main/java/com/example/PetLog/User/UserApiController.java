package com.example.PetLog.User;

import com.example.PetLog.User.dto.LoginRequestDTO;
import com.example.PetLog.User.dto.UserResponseDTO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController  //JSON 응답용
@RequestMapping("/api/user") //Android는 해당 url로 접근
public class UserApiController {

    @Autowired
    UserService userService;

    //로그인 api
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid LoginRequestDTO requestDTO){
        String id = requestDTO.getUser_login_id();
        String pw = requestDTO.getPassword();

        UserEntity userEntity = userService.findByLoginId(id);

        if(userEntity != null && userEntity.getPassword().equals(pw)){
            return ResponseEntity.ok(new UserResponseDTO(userEntity)); //성공
        } else {
            return ResponseEntity.status(401).body("로그인 실패");
        }
    }
}
