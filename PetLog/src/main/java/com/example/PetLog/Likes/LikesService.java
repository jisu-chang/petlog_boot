package com.example.PetLog.Likes;

import java.util.List;

public interface LikesService {

    List<LikesEntity> findByUserId(Long userId);

    void deleteByUserId(Long userId);

    void likeOnUser(Long postId, Long userId, String userLoginId);//좋아요 토클

    int getLikeCount(Long num); //게시글 좋아요 수

    boolean islikedByUser(Long num, Long userId, String userLoginId);

    boolean hasUserLiked(Long postId, Long userId);  //유저가 좋아요 눌렀는지 여부

    boolean isLikedOnSnack(Long snackId, Long userId, String userLoginId);

    int getSnackLikeCount(Long snackId);

    void likeOnSnack(Long snackId, Long userId, String userLoginId);

    int getLikeCountBySnackId(long snackId);

    boolean isLikedByUserOnSnack(long snackId, Long userId, String userLoginId);
}
