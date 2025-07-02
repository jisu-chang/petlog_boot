package com.example.PetLog.Diary;

import com.example.PetLog.Pet.PetDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface DiaryService {
    List<PetDTO> petByUser(); // 유저의 반려동물 목록 선택하기

    void save(DiaryEntity diaryEntity);

    List<DiaryDTO> findDiaryByUserId(Long userId);

    DiaryDTO detail(long diaryId);

    DiaryEntity detailEntity(Long diaryId);

    void update(DiaryEntity entity);

    void delete(Long diaryId);

    // 지수 추가 - 회원탈퇴
    List<DiaryEntity> findByDiaryUserId(Long userId);

    // 지수 추가 - 회원탈퇴
    void deleteByUserId(Long userId);

    // 페이징 처리용 메서드
    Page<DiaryDTO> findDiaryByUserIdPaged(Long userId, Pageable pageable);
}
