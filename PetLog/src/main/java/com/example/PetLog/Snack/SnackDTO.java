package com.example.PetLog.Snack;

import com.example.PetLog.User.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class SnackDTO {

    Long snackId;
    String snackTitle;
    String snackRecipe;
    MultipartFile snackImage;
    String snackImagename; //이미지 파일명 저장용
    LocalDate snackDate;
    int snackReadcnt;
    int commentCount; //entity x
    int likeCount; //entity x

    String getGrape; //entity x

    long userId;
    String userLoginId; //entity x
    UserEntity user;

    public SnackEntity entity() {
        return SnackEntity.builder()
                .snackId(snackId)
                .snackTitle(snackTitle)
                .snackRecipe(snackRecipe)
                .snackImage(snackImagename)
                .snackDate(snackDate)
                .snackReadcnt(snackReadcnt)
                .userId(userId)
                .build();
    }
}
