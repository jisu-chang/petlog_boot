package com.example.PetLog.Community;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;
import java.time.LocalDate;


@AllArgsConstructor
@NoArgsConstructor
@Data
public class CommunityDTO {
    Long post_id;
    Long user_id;
    String post_title;
    String post_content;
    MultipartFile post_image;
    int post_readcnt;
    LocalDate post_date;
    String post_type;


    public CommunityEntity entity() {
        String imageName = (post_image != null && !post_image.isEmpty())
                ? post_image.getOriginalFilename()
                : null;

        return CommunityEntity.builder()
                .postId(post_id)
                .userId(user_id)
                .postTitle(post_title)
                .postContent(post_content)
                .postImage(imageName) // 이미지 파일명
                .postReadcnt(post_readcnt)
                .postDate(post_date)
                .postType(post_type)
                .build();
    }
}
