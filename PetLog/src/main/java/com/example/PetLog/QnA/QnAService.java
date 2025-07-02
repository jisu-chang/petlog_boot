package com.example.PetLog.QnA;

import java.util.List;

public interface QnAService {

    List<QnAEntity> allout();

    void insertQnA(QnAEntity qnaEntity);

    QnAEntity DetailById(Long qnaId);

    QnAEntity UpdateById(Long qnaId);

    void UpdateSave(QnAEntity qnaEntity);

    QnAEntity DeleteById(Long qnaId);

    void DeleteSave(Long qnaId);

    List<QnAEntity> findByUserId(Long userId);

    void deleteByUserId(Long userId);

    //전체 QnA page
    List<QnAEntity> getQnAPage(int offset, int limit);
    int getTotalQnACount();

    //My Qna page
    List<QnAEntity> getUserQnAPage(Long userId, int offset, int limit);
    int getTotalUserQnACount(Long userId);
}
