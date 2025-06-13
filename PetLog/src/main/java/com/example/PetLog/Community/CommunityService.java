package com.example.PetLog.Community;

import com.example.PetLog.Comments.CommentsEntity;

import java.util.List;

public interface CommunityService {

    void insertpost(CommunityEntity communityEntity);

    List<CommunityEntity> allout();

    CommunityEntity findById(Long num);

    void readup(Long num);

    CommunityEntity updateById(Long unum);

    CommunityEntity deleteById(Long dnum);

    void deletesave(Long dnum);

    List<CommunityEntity> findByUserId(Long userId);

    void deleteByUserId(Long userId);

    //좋아요 기능
    void updateLikeCountForPost(Long postId);

    CommunityEntity getPostById(Long postId);

//    CommunityDTO getPost(Long postId);
}
