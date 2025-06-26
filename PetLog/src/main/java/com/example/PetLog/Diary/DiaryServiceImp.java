package com.example.PetLog.Diary;

import com.example.PetLog.Pet.PetDTO;
import com.example.PetLog.Pet.PetEntity;
import com.example.PetLog.Pet.PetRepository;
import com.example.PetLog.User.UserEntity;
import com.example.PetLog.User.UserRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DiaryServiceImp implements DiaryService {

DiaryRepository diaryRepository;
PetRepository petRepository;
UserRepository userRepository;

    public DiaryServiceImp(DiaryRepository diaryRepository, PetRepository petRepository, UserRepository userRepository) {
        this.diaryRepository = diaryRepository;
        this.petRepository = petRepository;
        this.userRepository = userRepository;
    }

@Override
public List<PetDTO> petByUser() {
    return null;
}

@Override
@Transactional
public void save(DiaryEntity diaryEntity) {
    diaryRepository.save(diaryEntity);

    Long userId = diaryEntity.getUserId();

    Optional<UserEntity> userOptional = userRepository.findById(userId);

    if (userOptional.isPresent()) {
        UserEntity user = userOptional.get();
        // 포도알 1개 적립
        user.setGrapeCount(user.getGrapeCount() + 1);
        // 갱신된 사용자 정보 저장
        userRepository.save(user);

        System.out.println("다이어리 작성 완료! 사용자 " + user.getUserLoginId() + "에게 포도알 1개가 적립되었습니다. 현재 포인트: " + user.getGrapeCount());
    } else {

        System.err.println("오류: 사용자 ID " + userId + "를 찾을 수 없어 포인트를 적립할 수 없습니다.");
    }

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