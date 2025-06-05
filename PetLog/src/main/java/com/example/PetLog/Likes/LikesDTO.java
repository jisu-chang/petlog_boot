package com.example.PetLog.Likes;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class LikesDTO {
    Long like_id;
    int user_id;
    int post_id;
    String user_login_id;
    int snack_id;
}
