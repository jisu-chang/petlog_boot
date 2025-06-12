package com.example.PetLog.Community;

import com.example.PetLog.Likes.LikesEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Repository
public interface CommunityRepository extends JpaRepository<CommunityEntity, Long> {

    @Transactional
    @Modifying
    @Query(value = "update community set post_readcnt=post_readcnt+1 where post_id=:post_id", nativeQuery = true)
    void readup(@Param("post_id") Long num);

    void deleteByUser_UserId(Long userId);

    List<CommunityEntity> findAllByUser_UserId(Long userId);

    @Modifying
    @Transactional
//    @Query(value = "select le1_0.like_id from likes le1_0 where le1_0.post_id=? and le1_0.user_id=? fetch first 1 row only", nativeQuery = true)
    @Query("UPDATE CommunityEntity c SET c.postReadcnt = :likeCount WHERE c.postId = :postId")
    void updateLikeCount(@Param("postId") Long postId, @Param("likeCount") int likeCount);

}
