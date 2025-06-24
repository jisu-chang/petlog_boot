package com.example.PetLog.Comments;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentsRepository extends JpaRepository<CommentsEntity, Long> {

    List<CommentsEntity> findAllByUser_UserId(Long userId);

    void deleteByUser_UserId(Long userId);

    //게시글 아이디로 댓글 조회
    @Query("SELECT c FROM CommentsEntity c WHERE c.community.postId = :postId ORDER BY c.parentId ASC, c.comId ASC")
    List<CommentsEntity> findCommentsByPostId(@Param("postId") Long postId);

}
