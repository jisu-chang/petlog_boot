package com.example.PetLog.Snack;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class SnackDTO {

    long snack_id;
    String snack_title;
    String snack_recipe;
    MultipartFile snack_image;
    String snack_imagename; //이미지 파일명 저장용
    LocalDate snack_date;
    int snack_readcnt;
    int comment_count; //entity x
    int like_count; //entity x

    String get_grape; //entity x

    long user_id;
    String user_login_id; //entity x

    public SnackEntity entity() {
        return SnackEntity.builder()
                .snackId(null)
                .snackTitle(snack_title)
                .snackRecipe(snack_recipe)
                .snackImage(snack_imagename)
                .snackDate(snack_date)
                .snackReadcnt(snack_readcnt)
                .userId(user_id)
                .build();
    }
}
