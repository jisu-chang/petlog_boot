package com.example.PetLog.Community;

import com.example.PetLog.Comments.CommentsEntity;
import com.example.PetLog.Likes.LikesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    public CommunityEntity findById(Long num) {
        return communityRepository.findById(num).orElse(null);
    }

    @Override
    public void readup(Long num) {
        communityRepository.readup(num);
    }

    @Override
    public CommunityEntity updateById(Long unum) {
        return communityRepository.findById(unum).orElse(null);
    }

    @Override
    public CommunityEntity deleteById(Long dnum) {
        return communityRepository.findById(dnum).orElse(null);
    }

    @Override
    public void deletesave(Long dnum) {
        communityRepository.deleteById(dnum);
    }

    @Override
    public List<CommunityEntity> findByUserId(Long userId) {
        return communityRepository.findAllByUser_UserId(userId);
    }

    @Override
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

//    @Override
//    public CommunityDTO getPost(Long postId) {
//        CommunityEntity entity = communityRepository.findById(postId)
//                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 존재하지 않습니다."));
//        return toDTO(entity);
//    }
//
//    private CommunityDTO toDTO(CommunityEntity entity) {
//        CommunityDTO dto = new CommunityDTO();
//        dto.setPostId(entity.getPostId());
//        dto.setPostTitle(entity.getPostTitle());
//        dto.setPostContent(entity.getPostContent());
//        dto.setUserId(entity.getUserId()); // 또는 userId
//        return dto;
//    }
}
