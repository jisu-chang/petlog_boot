package com.example.PetLog.Community;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CommunityDTO {
    Long post_id;
    int user_id;
    String post_title;
    String post_content;
    String post_image;
    int post_readcnt;
    String post_date;
//    String user_login_id;
//    int comment_count;
//    int like_count;
//    String profileimg;
    String post_type;
}
