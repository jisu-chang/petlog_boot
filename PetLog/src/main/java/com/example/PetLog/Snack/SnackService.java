package com.example.PetLog.Snack;

import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Service
public interface SnackService {

    void save(SnackEntity snackEntity);

    List<SnackDTO> out();

    SnackDTO detail(long snackId);

    SnackEntity getSnack(Long snackId);

    void update(SnackEntity entity);

    void delete(Long snackId);

    //지수 추가 - 회원탈퇴
    List<SnackEntity> findByUserId(Long userId);
    //지수 추가 - 회원탈퇴
    void deleteByUserId(Long userId);

    SnackEntity getSnackById(Long snackId);

    List<SnackDTO> searchSnacks(String postType, String keyword);

    Integer getCommentCount(Long snackId);

    Integer getLikeCount(Long snackId);

    //page
    Page<SnackDTO> findPagedSnacks(Pageable pageable);
}
