package com.example.PetLog.Diary;

import com.example.PetLog.Pet.PetDTO;
import com.example.PetLog.Pet.PetEntity;
import com.example.PetLog.Pet.PetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

}
