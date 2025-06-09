package com.example.PetLog.Diary;

import com.example.PetLog.Pet.PetDTO;
import com.example.PetLog.Pet.PetEntity;
import com.example.PetLog.Pet.PetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DiaryServiceImp implements DiaryService {

@Autowired
DiaryRepository diaryRepository;

@Autowired
PetRepository petRepository;

@Override
public List<PetDTO> petByUser() {
    return null;
}

@Override
public void save(DiaryEntity diaryEntity) {
    diaryRepository.save(diaryEntity);
}

@Override
public List<DiaryDTO> findDiaryByUserId(Long userId) {

    List<DiaryEntity> diaryEntities = diaryRepository.findByUserId(userId);

    List<PetEntity> petList = petRepository.findByUserUserId(userId);
    Map<Long, String> petIdNameMap = petList.stream()
            .collect(Collectors.toMap(PetEntity::getPetId, PetEntity::getPetName));

    List<DiaryDTO> diaryDTOList = diaryEntities.stream()
            .map(entity -> {
                DiaryDTO dto = new DiaryDTO();
                dto.setDiaryId(entity.getDiaryId());
                dto.setDiaryTitle(entity.getDiaryTitle());
                dto.setDiaryDate(entity.getDiaryDate());
                dto.setDiaryImageName(entity.getDiaryImage());
                dto.setDiaryContent(entity.getDiaryContent());

                dto.setPetName(petIdNameMap.getOrDefault(entity.getPetId(), "이름없음"));

                return dto;
            })
            .collect(Collectors.toList());

    return diaryDTOList;
}

@Override
public DiaryDTO detail(long diaryId) {
    DiaryEntity diaryEntity = diaryRepository.findById(diaryId)
            .orElseThrow(() -> new RuntimeException("일기를 찾을 수 없습니다: " + diaryId));

    PetEntity petEntity = petRepository.findById(diaryEntity.getPetId()).orElse(null);
    String petName = (petEntity != null) ? petEntity.getPetName() : "이름없음";

    DiaryDTO dto = new DiaryDTO();
    dto.setDiaryId(diaryEntity.getDiaryId());
    dto.setDiaryTitle(diaryEntity.getDiaryTitle());
    dto.setDiaryDate(diaryEntity.getDiaryDate());
    dto.setDiaryImageName(diaryEntity.getDiaryImage());
    dto.setDiaryContent(diaryEntity.getDiaryContent());
    dto.setPetName(petName); // 기존 오류 수정 (petEntity가 null일 경우 대비)

    return dto;
}

@Override
public DiaryEntity detailEntity(Long diaryId) { //diaryId에 해당하는 데이터 가져오기
    return diaryRepository.findById(diaryId)
            .orElseThrow(() -> new RuntimeException("일기를 찾을 수 없습니다: " + diaryId));
}

    @Override
    public void update(DiaryEntity entity) {
    diaryRepository.save(entity);
    }

    @Override
    public void delete(Long diaryId) {
        diaryRepository.deleteById(diaryId);
    }

    //지수 추가 - 회원탈퇴
    @Override
    public List<DiaryEntity> findByDiaryUserId(Long userId) {
        return diaryRepository.findAllByUser_UserId(userId);
    }

    //지수 추가 - 회원탈퇴
    @Override
    public void deleteByUserId(Long userId) {
        diaryRepository.deleteByUser_UserId(userId);
    }

}