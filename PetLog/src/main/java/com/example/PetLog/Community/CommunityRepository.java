package com.example.PetLog.Community;

import com.example.PetLog.Likes.LikesEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestParam;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Repository
public interface CommunityRepository extends JpaRepository<CommunityEntity, Long> {

    @Transactional
    @Modifying
    @Query(value = "update community set post_readcnt=post_readcnt+1 where post_id=:post_id", nativeQuery = true)
    void readup(@Param("post_id") Long postId);

    void deleteByUser_UserId(Long userId);

    List<CommunityEntity> findAllByUser_UserId(Long userId);

    @Modifying
    @Transactional
    @Query("UPDATE CommunityEntity c SET c.postReadcnt = :likeCount WHERE c.postId = :postId")
    void updateLikeCount(@Param("postId") Long postId, @Param("likeCount") int likeCount);

    List<CommunityEntity> findByPostType(String notice);

    @Modifying
    @Transactional
    @Query("UPDATE SnackEntity s SET s.snackReadcnt = :likeCount WHERE s.snackId = :snackId")
    void updateLikeBySnackCount(@Param("snackId") Long snackId, @Param("likeCount") int likeCount);


    // 제목+타입으로 검색 (공지/커뮤니티 구분용)
    List<CommunityEntity> findByPostTitleContainingAndPostType(String keyword, String postType);

    // 내용+타입으로 검색
    List<CommunityEntity> findByPostContentContainingAndPostType(String keyword, String postType);

    int countByUserId(Long userId);

    List<CommunityEntity> findByUser_UserId(Long userId);

    //커뮤니티 page
    @Query(value =
            "SELECT * FROM (" +
                    "  SELECT ce.*, ROWNUM rn FROM (" +
                    "    SELECT * FROM community WHERE user_id IS NOT NULL AND post_type = :postType ORDER BY post_date DESC" +
                    "  ) ce WHERE ROWNUM <= :endRow" +
                    ") WHERE rn >= :startRow",
            nativeQuery = true)
    List<CommunityEntity> findByPostTypeWithPaging(
            @Param("postType") String postType,
            @Param("startRow") int startRow,
            @Param("endRow") int endRow);

    long countByPostType(String postType);

    //공지사항 page
    @Query(value = "SELECT * FROM (" +
            " SELECT a.*, ROWNUM rnum FROM (" +
            "   SELECT * FROM community WHERE post_type = 'notice' ORDER BY post_date DESC" +
            " ) a WHERE ROWNUM <= :endRow" +
            ") WHERE rnum >= :startRow",
            nativeQuery = true)
    List<CommunityEntity> findNoticePostsByRowBounds(@Param("startRow") int startRow,
                                                     @Param("endRow") int endRow);

    @Query(value = "SELECT COUNT(*) FROM community WHERE post_type = 'notice'", nativeQuery = true)
    int countNoticePosts();
}
