package com.example.PetLog.Community;

import com.example.PetLog.Comments.CommentsEntity;

import java.util.List;

public interface CommunityService {

    void insertpost(CommunityEntity communityEntity);

    List<CommunityEntity> allout();

    CommunityEntity findById(Long postId);

    void readup(Long postId);

    CommunityEntity updateById(Long postId);

    CommunityEntity deleteById(Long postId);

    void deletesave(Long postId);

    List<CommunityEntity> findByUserId(Long userId);

    void deleteByUserId(Long userId);

    //좋아요 기능
    void updateLikeCountForPost(Long postId);

    CommunityEntity getPostById(Long postId);

    List<CommunityEntity> getNoticePost();

}
