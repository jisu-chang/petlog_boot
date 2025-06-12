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
    Long postId;
    Long userId;
    String postTitle;
    String postContent;
    MultipartFile postImage;
    int postReadcnt;
    LocalDate postDate;
    String postType;


    public CommunityEntity entity() {
        String imageName = (postImage != null && !postImage.isEmpty())
                ? postImage.getOriginalFilename()
                : null;

        return CommunityEntity.builder()
                .postId(postId)
                .userId(userId)
                .postTitle(postTitle)
                .postContent(postContent)
                .postImage(imageName) // 이미지 파일명
                .postReadcnt(postReadcnt)
                .postDate(postDate)
                .postType(postType)
                .build();
    }
}
