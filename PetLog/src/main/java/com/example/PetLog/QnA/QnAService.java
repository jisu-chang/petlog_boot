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
}
