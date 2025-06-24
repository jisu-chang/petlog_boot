package com.example.PetLog.Comments;

import java.util.List;

public interface CommentsService {

    List<CommentsEntity> findByUserId(Long userId);

    void deleteByUserId(Long userId);

    void saveComment(CommentsDTO commentsDTO);

    List<CommentsDTO> getCommentsByPostId(Long postId);

    List<CommentsEntity> getSnackComments(Long snackId);

}
