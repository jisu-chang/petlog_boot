package com.example.PetLog.Likes;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LikesRepository extends JpaRepository<LikesEntity, Long> {

    // 특정 사용자에 대한 모든 좋아요 찾기 - 회원탈퇴
    List<LikesEntity> findAllByUser_UserId(Long userId);

    //특정 사용자에 대한 좋아요 삭제 - 회원탈퇴
    void deleteByUser_UserId(Long userId);

    // 좋아요 있는지 확인
    LikesEntity findByPostIdAndUserIdAndUserLoginId(Long postId, Long userId, String userLoginId);

    //좋아요 삭제(기존 좋아요 삭제처리)
    void delete(LikesEntity existingLike);

    // 좋아요 수 카운팅
    int countByPostId(Long postId);

    //좋아요 유무
    boolean existsByPostIdAndUserId(Long postId, Long userId);
}
