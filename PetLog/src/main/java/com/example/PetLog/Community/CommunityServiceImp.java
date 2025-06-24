package com.example.PetLog.Community;

import com.example.PetLog.Comments.CommentsEntity;
import com.example.PetLog.Likes.LikesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CommunityServiceImp implements CommunityService{

    @Autowired
    CommunityRepository communityRepository;
    @Autowired
    LikesRepository likesRepository;

    @Override
    public void insertpost(CommunityEntity communityEntity) {
        communityRepository.save(communityEntity);
    }

    @Override
    public List<CommunityEntity> allout() {
        return communityRepository.findAll();
    }

    @Override
    public CommunityEntity findById(Long postId) {
        return communityRepository.findById(postId).orElse(null);
    }

    @Override
    public void readup(Long postId) {
        communityRepository.readup(postId);
    }

    @Override
    public CommunityEntity updateById(Long postId) {
        return communityRepository.findById(postId).orElse(null);
    }

    @Override
    public CommunityEntity deleteById(Long postId) {
        return communityRepository.findById(postId).orElse(null);
    }

    @Override
    public void deletesave(Long postId) {
        communityRepository.deleteById(postId);
    }

    @Override
    public List<CommunityEntity> findByUserId(Long userId) {
        return communityRepository.findAllByUser_UserId(userId);
    }

    @Override
    @Transactional
    public void deleteByUserId(Long userId) {
        communityRepository.deleteByUser_UserId(userId);
    }

    //좋아요 기능-------------------------------------
    @Override
    public void updateLikeCountForPost(Long postId) {
        int likeCount = likesRepository.countByPostId(postId); //좋아요 수 조회
        communityRepository.updateLikeCount(postId,likeCount); //게시글에서 좋아요 수 업데이트
    }

    @Override
    public CommunityEntity getPostById(Long postId) {
        return communityRepository.findById(postId).orElse(null);
    }

    //공지사항 출력
    @Override
    public List<CommunityEntity> getNoticePost() {
        return communityRepository.findByPostType("notice");
    }

}
