package com.example.PetLog.Likes;

import java.util.List;

public interface LikesService {

    List<LikesEntity> findByUserId(Long userId);

    void deleteByUserId(Long userId);

    void likeOnUser(Long postId, Long userId, String userLoginId);//좋아요 토클

    int getLikeCount(Long num); //게시글 좋아요 수

    boolean islikedByUser(Long num, Long userId, String userLoginId);

    boolean isLikedByUserOnSnack(Long snackId, Long userId, String userLoginId);

    int getSnackLikeCount(Long snackId);

    void likeOnUserSnackId(Long snackId, Long userId, String userLoginId);

}
