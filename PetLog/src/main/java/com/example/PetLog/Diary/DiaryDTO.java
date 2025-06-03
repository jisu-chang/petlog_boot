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

    int diaryId;
    String diaryTitle;
    LocalDate diaryDate;
    MultipartFile diaryImage;
    String diaryContent;

    int getGrape; //일기 쓰고 포도알 적립 되었는지 확인하는 용도!

    //UserDTO & PetDTO 필요함
    int userId;
    int petId;

    String petName;

    public DiaryEntity entity() {
        String filename = (diaryImage != null) ? diaryImage.getOriginalFilename() : null;
        return null;//수정해야함!!
    }
}
