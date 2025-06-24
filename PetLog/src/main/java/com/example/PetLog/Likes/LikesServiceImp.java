package com.example.PetLog.Likes;

import com.example.PetLog.Comments.CommentsRepository;
import com.example.PetLog.Comments.CommentsService;
import com.example.PetLog.Community.CommunityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class LikesServiceImp implements LikesService {

    @Autowired
    LikesRepository likesRepository;
    @Autowired
    CommunityService communityService;
    @Autowired
    CommentsRepository commentsRepository;

    @Override
    public List<LikesEntity> findByUserId(Long userId) {
        return likesRepository.findAllByUser_UserId(userId);
    }

    @Override
    @Transactional
    public void deleteByUserId(Long userId) {
        likesRepository.deleteByUser_UserId(userId);
    }

    @Override
    @Transactional  //UPDATE, DELETE 등의 데이터 변경 작업이 트랜잭션 내에서 처리되도록 -> 쿼리 실행x
    public void likeOnUser(Long postId, Long userId, String userLoginId) {
        // 이미 좋아요가 존재하는지 확인
        LikesEntity existingLike = likesRepository.findByPostIdAndUserIdAndUserLoginId(postId, userId, userLoginId);

        if (existingLike != null) {
            // 좋아요가 존재하면 삭제 (좋아요 취소)
            likesRepository.delete(existingLike);  // 삭제할 때 LikesEntity 사용
        } else if (userLoginId != null && !userLoginId.isEmpty()) {
            // 좋아요가 없으면 추가
            LikesEntity newLike = new LikesEntity();  // 새로운 LikesEntity 생성
            newLike.setPostId(postId);
            newLike.setUserId(userId);  // Long 타입으로 처리
            newLike.setUserLoginId(userLoginId);  // 로그인 아이디 설정
            likesRepository.save(newLike);  // 새로운 좋아요 추가
        } else {
            throw new IllegalArgumentException("User login ID cannot be null or empty");
        }

        // 게시글의 좋아요 수 갱신
        communityService.updateLikeCountForPost(postId);
    }

    @Override //게시글에 대한 좋아요 수 가져오기
    public int getLikeCount(Long num) {
        return likesRepository.countByPostId(num);
    }

    @Override //사용자가 해당 게시글 좋아요 눌렀는지 확인
    public boolean islikedByUser(Long num, Long userId, String userLoginId) {
        LikesEntity existing = likesRepository.findByPostIdAndUserIdAndUserLoginId(num, userId, userLoginId);
        return existing != null;
    }

    @Override
    public boolean isLikedByUserOnSnack(Long snackId, Long userId, String userLoginId) {
        return likesRepository.existsBySnackIdAndUserIdAndUserLoginId(snackId, userId, userLoginId);
    }

    @Override
    public int getSnackLikeCount(Long snackId) {
        return likesRepository.countBySnack_SnackId(snackId);
    }

    @Override
    public void likeOnUserSnackId(Long snackId, Long userId, String userLoginId) {
        // 이미 좋아요가 존재하는지 확인
        LikesEntity existingLike = likesRepository.findBySnackIdAndUserIdAndUserLoginId(snackId, userId, userLoginId);

        if (existingLike != null) {
            // 좋아요가 존재하면 삭제 (좋아요 취소)
            likesRepository.delete(existingLike);  // 삭제할 때 LikesEntity 사용
        } else if (userLoginId != null && !userLoginId.isEmpty()) {
            // 좋아요가 없으면 추가
            LikesEntity newLike = new LikesEntity();  // 새로운 LikesEntity 생성
            newLike.setSnackId(snackId);
            newLike.setUserId(userId);  // Long 타입으로 처리
            newLike.setUserLoginId(userLoginId);  // 로그인 아이디 설정
            likesRepository.save(newLike);  // 새로운 좋아요 추가
        } else {
            throw new IllegalArgumentException("User login ID cannot be null or empty");
        }
        // 게시글의 좋아요 수 갱신
        communityService.updateLikeCountForSnack(snackId);
    }

}

