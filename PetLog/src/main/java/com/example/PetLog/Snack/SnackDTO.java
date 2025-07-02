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

    private Long snackId;
    private String snackTitle;
    private String snackRecipe;

    // 업로드용
    private MultipartFile snackImage;

    // 실제 DB에 저장된 이미지 파일명
    private String snackImagename;

    private LocalDate snackDate;
    private int snackReadcnt;

    // 조회 전용
    private int commentCount;
    private int likeCount;
    private String getGrape;
    private String userLoginId;

    private long userId;
    private UserEntity user;

    // ✅ Entity로 변환
    public SnackEntity entity() {
        return SnackEntity.builder()
                .snackId(snackId)
                .snackTitle(snackTitle)
                .snackRecipe(snackRecipe)
                .snackImage(snackImagename) // Entity에서는 이미지 이름만 저장
                .snackDate(snackDate != null ? snackDate : LocalDate.now())
                .snackReadcnt(snackReadcnt)
                .userId(userId)
                .build();
    }

    public SnackDTO(SnackEntity entity) {
        this.snackId = entity.getSnackId();
        this.snackTitle = entity.getSnackTitle();
        this.snackRecipe = entity.getSnackRecipe();
        this.snackImagename = entity.getSnackImage();
        this.snackDate = entity.getSnackDate();
        this.snackReadcnt = entity.getSnackReadcnt();
        this.userId = entity.getUserId();
        if (entity.getUser() != null) {
            this.userLoginId = entity.getUser().getUserLoginId();
        }
    }
}
