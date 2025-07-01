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
    MultipartFile postImage;  //사용자가 업로드한 파일
    String postImageName;  //실제 저장 된 파일 이름
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
                .postImage(postImageName) // 이미지 파일명
                .postReadcnt(postReadcnt)
                .postDate(postDate)
                .postType(this.postType)
                .build();
    }

}
