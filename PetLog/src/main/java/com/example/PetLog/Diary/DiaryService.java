package com.example.PetLog.Diary;

import com.example.PetLog.Pet.PetDTO;
import org.springframework.stereotype.Service;
import java.util.List;


@Service
public interface DiaryService {
    List<PetDTO> petByUser(); // 유저의 반려동물 목록 선택하기

    void save(DiaryEntity diaryEntity);

    List<DiaryDTO> findDiaryByUserId(Long userId);

    DiaryDTO detail(long diaryId);

    DiaryEntity detailEntity(Long diaryId);
}
