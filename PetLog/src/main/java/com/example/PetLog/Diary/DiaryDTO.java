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

    int getGrape;

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
                .diaryImage(diaryImageName) // ★ 이게 DB에 들어감
                .diaryContent(diaryContent)
                .getGrape(getGrape)
                .userId(userId)
                .petId(petId)
                .build();
    }

    public void setDiaryImageName(String filename) {
        this.diaryImageName = filename;
    }
}
