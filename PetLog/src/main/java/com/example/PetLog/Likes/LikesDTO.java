package com.example.PetLog.Likes;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class LikesDTO {
    Long likeId;
    Long userId;
    Long postId;
    String userLoginId;
    Long snackId;


}
