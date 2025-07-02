package com.example.PetLog.Diary;

import com.example.PetLog.Community.CommunityEntity;
import com.example.PetLog.Pet.PetDTO;
import com.example.PetLog.Pet.PetEntity;
import com.example.PetLog.Pet.PetRepository;
import com.example.PetLog.User.UserEntity;
import com.example.PetLog.User.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DiaryServiceImp implements DiaryService {

    private final DiaryRepository diaryRepository;
    private final PetRepository petRepository;
    private final UserRepository userRepository;
    String path = System.getProperty("user.dir") + "/src/main/resources/static/image";

    @Autowired
    public DiaryServiceImp(DiaryRepository diaryRepository, PetRepository petRepository, UserRepository userRepository) {
        this.diaryRepository = diaryRepository;
        this.petRepository = petRepository;
        this.userRepository = userRepository;
    }

    @Override
    public List<PetDTO> petByUser() {
        return null; // 필요시 구현
    }

    @Override
    @Transactional
    public void save(DiaryEntity diaryEntity) {
        diaryRepository.save(diaryEntity);

        Long userId = diaryEntity.getUserId();

        Optional<UserEntity> userOptional = userRepository.findById(userId);

        if (userOptional.isPresent()) {
            UserEntity user = userOptional.get();
            user.setGrapeCount(user.getGrapeCount() + 1);
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

        return diaryEntities.stream()
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
        dto.setPetName(petName);

        return dto;
    }

    @Override
    public DiaryEntity detailEntity(Long diaryId) {
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

    // 지수 추가 - 회원탈퇴
    @Override
    public List<DiaryEntity> findByDiaryUserId(Long userId) {
        return diaryRepository.findAllByUser_UserId(userId);
    }

    // 지수 추가 - 회원탈퇴
    @Override
    @Transactional
    public void deleteByUserId(Long userId) {
        // 해당 유저의 게시글 불러오기
        List<DiaryEntity> posts = diaryRepository.findByUser_UserId(userId);

        // 각 게시글에 등록된 이미지 파일 삭제
        for (DiaryEntity post : posts) {
            if (post.getDiaryImage()!= null && !post.getDiaryImage().equals("default.png")) {
                File file = new File(path + File.separator + post.getDiaryImage());
                if (file.exists()) {
                    file.delete();
                }
            }
        }

        diaryRepository.deleteByUser_UserId(userId);
    }

    // 페이징 처리 (Oracle 11g 이하 네이티브 쿼리 사용)
    @Override
    public Page<DiaryDTO> findDiaryByUserIdPaged(Long userId, Pageable pageable) {
        int offset = (int) pageable.getOffset();
        int limit = pageable.getPageSize();

        List<DiaryEntity> diaryEntities = diaryRepository.findDiaryByUserIdPagedNative(userId, offset, limit);

        // 총 개수 조회
        int total = diaryRepository.countByUserIdNative(userId);

        List<Long> petIds = diaryEntities.stream()
                .map(DiaryEntity::getPetId)
                .distinct()
                .toList();

        Map<Long, String> petIdNameMap = petRepository.findAllById(petIds).stream()
                .collect(Collectors.toMap(PetEntity::getPetId, PetEntity::getPetName));

        List<DiaryDTO> diaryDTOs = diaryEntities.stream()
                .peek(diary -> diary.setPetName(petIdNameMap.getOrDefault(diary.getPetId(), "이름없음")))
                .map(DiaryDTO::new)
                .toList();

        return new PageImpl<>(diaryDTOs, pageable, total);
    }
}
