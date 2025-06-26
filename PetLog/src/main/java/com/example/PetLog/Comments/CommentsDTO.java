package com.example.PetLog.Comments;

import com.example.PetLog.User.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CommentsDTO {
    Long com_id;
    Long user_id;
    Long post_id;
    String com_com;
    int parent_id;
    int depth;
    Long snack_id;

    UserEntity user;
    String userLoginId;

    public String getProfileimg() {
        return (user != null) ? user.getProfileimg() : null;
    }
}