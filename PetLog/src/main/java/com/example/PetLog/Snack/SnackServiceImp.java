package com.example.PetLog.Snack;

import com.example.PetLog.Comments.CommentsRepository;
import com.example.PetLog.Diary.DiaryEntity;
import com.example.PetLog.Likes.LikesRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Service
public class SnackServiceImp implements SnackService {

    @Autowired
    SnackRepository snackRepository;
    @Autowired
    LikesRepository likesRepository;
    @Autowired
    CommentsRepository commentsRepository;
    String path = "C:/petlog-uploads/snack";

    @Override
    public void save(SnackEntity snackEntity) {
        snackRepository.save(snackEntity);
    }

    @Override
    public List<SnackDTO> out() {
        List<SnackEntity> list = snackRepository.findAll();

        return list.stream().map(entity -> {
            SnackDTO dto = new SnackDTO(entity); // DTO 생성자 활용
            if (entity.getUser() != null) {
                dto.setUserLoginId(entity.getUser().getUserLoginId());
            }
            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    public SnackDTO detail(long snackId) {
        SnackEntity snackEntity = snackRepository.findById(snackId)
                .orElseThrow(() -> new RuntimeException("간식 레시피를 찾을 수 없습니다: " + snackId));

        SnackDTO dto = new SnackDTO(snackEntity);
        if (snackEntity.getUser() != null) {
            dto.setUserLoginId(snackEntity.getUser().getUserLoginId());
        } else {
            dto.setUserLoginId("알 수 없음");
        }
        dto.setUser(snackEntity.getUser());
        return dto;
    }

    @Override
    public SnackEntity getSnack(Long snackId) {
        return snackRepository.findById(snackId)
                .orElseThrow(() -> new RuntimeException("레시피를 찾을 수 없습니다: " + snackId));
    }

    @Override
    public void update(SnackEntity entity) {
        snackRepository.save(entity);
    }

    @Override
    public void delete(Long snackId) {
        // 댓글, 좋아요 삭제 먼저
        commentsRepository.deleteBysnackId(snackId);
        likesRepository.deleteBysnackId(snackId);
        //이미지 삭제 처리
        SnackEntity snack = snackRepository.findById(snackId).orElse(null);
        if(snack != null && snack.getSnackImage() != null && !snack.getSnackImage().equals("default.png")){
            File file = new File(path + File.separator + snack.getSnackImage());
            if(file.exists()){
                file.delete();
            }
        }
        snackRepository.deleteById(snackId);
    }

    // 회원 탈퇴 관련
    @Override
    public List<SnackEntity> findByUserId(Long userId) {
        return snackRepository.findAllByUser_UserId(userId);
    }

    @Override
    @Transactional
    public void deleteByUserId(Long userId) {
        // 해당 유저의 게시글 불러오기
        List<SnackEntity> posts = snackRepository.findByUser_UserId(userId);

        // 각 게시글에 등록된 이미지 파일 삭제
        for (SnackEntity post : posts) {
            if (post.getSnackImage()!= null && !post.getSnackImage().equals("default.png")) {
                File file = new File(path + File.separator + post.getSnackImage());
                if (file.exists()) {
                    file.delete();
                }
            }
        }
        snackRepository.deleteByUser_UserId(userId);
    }

    @Override
    public SnackEntity getSnackById(Long snackId) {
        return snackRepository.findById(snackId).orElse(null);
    }

    @Override
    public List<SnackDTO> searchSnacks(String postType, String keyword) {
        List<SnackEntity> entities;

        if ("title".equals(postType)) {
            entities = snackRepository.findBySnackTitleContaining(keyword);
        } else if ("content".equals(postType)) {
            entities = snackRepository.findBySnackRecipeContaining(keyword);
        } else {
            entities = List.of();
        }

        return entities.stream().map(entity -> {
            SnackDTO dto = new SnackDTO(entity);
            if (entity.getUser() != null) {
                dto.setUserLoginId(entity.getUser().getUserLoginId());
            }
            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    public Integer getCommentCount(Long snackId) {
        return commentsRepository.countBySnack_SnackId(snackId);
    }

    @Override
    public Integer getLikeCount(Long snackId) {
        return likesRepository.countBySnack_SnackId(snackId);
    }

    @Override
    public Page<SnackDTO> findPagedSnacks(Pageable pageable) {
        // 페이징 오프셋, 한 페이지 크기
        int offset = (int) pageable.getOffset();
        int limit = pageable.getPageSize();

        // 페이징 네이티브 쿼리 등 커스텀 메서드 필요
        List<SnackEntity> snacks = snackRepository.findSnacksPaged(offset, limit);
        int total = snackRepository.countAllSnacks();

        List<SnackDTO> snackDTOs = snacks.stream()
                .map(entity -> {
                    SnackDTO dto = new SnackDTO(entity);
                    if (entity.getUser() != null) {
                        dto.setUserLoginId(entity.getUser().getUserLoginId());
                    }
                    return dto;
                })
                .collect(Collectors.toList());

        return new PageImpl<>(snackDTOs, pageable, total);
    }
}

