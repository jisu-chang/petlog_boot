package com.example.PetLog.Community;

import com.example.PetLog.Comments.CommentsEntity;
import com.example.PetLog.Comments.CommentsRepository;
import com.example.PetLog.Likes.LikesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CommunityServiceImp implements CommunityService{

    @Autowired
    CommunityRepository communityRepository;
    @Autowired
    LikesRepository likesRepository;
    @Autowired
    CommentsRepository commentsRepository;

    String path = "C:/petlog-uploads/community";

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
        // 1. 댓글 삭제
        commentsRepository.deleteByPostId(postId);
        // 2. 좋아요 삭제
        likesRepository.deleteByPostId(postId);
        // 3. 이미지 삭제
        CommunityEntity post = communityRepository.findById(postId).orElse(null);
        if(post != null && post.getPostImage() != null && !post.getPostImage().equals("default.png")){
            File file = new File(path + File.separator + post.getPostImage());
            if(file.exists()){
                file.delete();
            }
        }
        // 4. 게시글 삭제
        communityRepository.deleteById(postId);
    }

    @Override
    public List<CommunityEntity> findByUserId(Long userId) {
        return communityRepository.findAllByUser_UserId(userId);
    }

    @Override
    @Transactional
    public void deleteByUserId(Long userId) {
        // 해당 유저의 커뮤니티 글 불러오기
        List<CommunityEntity> posts = communityRepository.findByUser_UserId(userId);

        // 각 게시글에 등록된 이미지 파일 삭제
        for (CommunityEntity post : posts) {
            if (post.getPostImage()!= null && !post.getPostImage().equals("default.png")) {
                File file = new File(path + File.separator + post.getPostImage());
                if (file.exists()) {
                    file.delete();
                }
            }
        }
        // DB에서 게시글 삭제
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
        List<CommunityEntity> noticePosts = communityRepository.findByPostType("notice");

        // 정렬: postDate 최신순, 같으면 postId 내림차순
        noticePosts.sort((a, b) -> {
            int dateCompare = b.getPostDate().compareTo(a.getPostDate());
            if (dateCompare != 0) return dateCompare;
            return Long.compare(b.getPostId(), a.getPostId());
        });

        return noticePosts;
    }

    @Override
    public void updateLikeCountForSnack(Long snackId) {
        int likeCount = likesRepository.countBySnack_SnackId(snackId); //좋아요 수 조회
        communityRepository.updateLikeBySnackCount(snackId,likeCount); //게시글에서 좋아요 수 업데이트
    }

    @Override
    public List<CommunityEntity> searchCommunity(String keyword, String postType) {
        if (postType.equals("title")) {
            return communityRepository.findByPostTitleContainingAndPostType(keyword, "normal");
        } else {
            return communityRepository.findByPostContentContainingAndPostType(keyword, "normal");
        }
    }

    @Override
    public List<CommunityEntity> searchNotice(String keyword, String postType) {
        if (postType.equals("title")) {
            return communityRepository.findByPostTitleContainingAndPostType(keyword, "notice");
        } else {
            return communityRepository.findByPostContentContainingAndPostType(keyword, "notice");
        }
    }

    public Map<Long, Integer> getCommentCountsByList(List<CommunityEntity> posts) {
        Map<Long, Integer> map = new HashMap<>();
        for (CommunityEntity post : posts) {
            int count = commentsRepository.countByCommunity_PostId(post.getPostId());
            map.put(post.getPostId(), count);
        }
        return map;
    }

    public Map<Long, Integer> getLikeCountsByList(List<CommunityEntity> posts) {
        Map<Long, Integer> map = new HashMap<>();
        for (CommunityEntity post : posts) {
            int count = likesRepository.countByPostId(post.getPostId());
            map.put(post.getPostId(), count);
        }
        return map;
    }

    //커뮤니티 page
    @Override
    public Page<CommunityEntity> getPostsByType(String postType, Pageable pageable) {
        int page = pageable.getPageNumber();
        int size = pageable.getPageSize();

        int startRow = page * size + 1;
        int endRow = startRow + size - 1;

        // 수동 페이징 쿼리 호출 (Repository에 아래 메서드 필요)
        List<CommunityEntity> content = communityRepository.findByPostTypeWithPaging(postType, startRow, endRow);

        // 전체 개수 조회 (Repository에 countByPostType 메서드 필요)
        long total = communityRepository.countByPostType(postType);

        return new PageImpl<>(content, pageable, total);
    }

    //공지사항 page
    @Override
    public List<CommunityEntity> getNoticePostsPaging(int startRow, int endRow) {
        return communityRepository.findNoticePostsByRowBounds(startRow, endRow);
    }

    @Override
    public int countNoticePosts() {
        return communityRepository.countNoticePosts();
    }
}
