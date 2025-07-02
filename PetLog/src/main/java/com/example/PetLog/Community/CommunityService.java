package com.example.PetLog.Community;

import com.example.PetLog.Comments.CommentsEntity;

import java.util.List;
import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

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

    void updateLikeCountForSnack(Long snackId);

    List<CommunityEntity> searchNotice(String keyword, String postType);

    List<CommunityEntity> searchCommunity(String keyword, String postType);

    Map<Long, Integer> getCommentCountsByList(List<CommunityEntity> communityResults);

    Map<Long, Integer> getLikeCountsByList(List<CommunityEntity> communityResults);

    // 커뮤니티 page
    public Page<CommunityEntity> getPostsByType(String postType, Pageable pageable);

    // 공지사항 page
    List<CommunityEntity> getNoticePostsPaging(int startRow, int endRow);
    int countNoticePosts();
}
