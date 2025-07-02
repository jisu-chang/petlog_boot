package com.example.PetLog.Diary;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class DiaryDTO {

    Long diaryId;
    String diaryTitle;
    LocalDate diaryDate;
    MultipartFile diaryImage;
    String diaryContent;

    int grapeCount;

    Long userId;
    Long petId;
    String petName;

    // ★ 파일명 저장용 필드
    String diaryImageName;

    public DiaryEntity entity() {
        return DiaryEntity.builder()
                .diaryId(diaryId)
                .diaryTitle(diaryTitle)
                .diaryDate(diaryDate)
                .diaryImage(diaryImageName)
                .diaryContent(diaryContent)
                .getGrape(grapeCount)   // entity 필드명 맞춰서
                .userId(userId)
                .petId(petId)
                .build();
    }

    public DiaryDTO(DiaryEntity entity) {
        this.diaryId = entity.getDiaryId();
        this.diaryTitle = entity.getDiaryTitle();
        this.diaryDate = entity.getDiaryDate();
        this.diaryContent = entity.getDiaryContent();
        this.diaryImageName = entity.getDiaryImage();
        this.grapeCount = entity.getGetGrape();  // 필드명 일치
        this.userId = entity.getUserId();
        this.petId = entity.getPetId();
        this.petName = entity.getPetName();
    }

    public void setDiaryImageName(String filename) {
        this.diaryImageName = filename;
    }
}
