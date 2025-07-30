package com.example.PetLog.Comments;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentsRepository extends JpaRepository<CommentsEntity, Long> {

    List<CommentsEntity> findAllByUser_UserId(Long userId);

    void deleteByUser_UserId(Long userId);

    //게시글 아이디로 댓글 조회
    @Query("SELECT c FROM CommentsEntity c JOIN FETCH c.user WHERE c.community.postId = :postId ORDER BY c.parentId ASC, c.comId ASC")
    List<CommentsEntity> findCommentsByPostId(@Param("postId") Long postId);

    @Query("SELECT c FROM CommentsEntity c JOIN FETCH c.user WHERE c.snack.snackId = :snackId ORDER BY c.parentId ASC, c.comId ASC")
    List<CommentsEntity> findBySnack_SnackId(@Param("snackId") Long snackId);

    @Modifying
    @Transactional
    @Query("DELETE FROM CommentsEntity c WHERE c.community.postId = :postId")
    void deleteByPostId(Long postId);

    @Modifying
    @Transactional
    @Query("DELETE FROM CommentsEntity c WHERE c.snack.snackId = :snackId")
    void deleteBysnackId(@Param("snackId") Long snackId);

    int countByCommunity_PostId(Long postId);

    Integer countBySnack_SnackId(Long snackId);

    int countByUser_UserId(Long userId);
}
