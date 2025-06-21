package com.example.PetLog.QnA;

import java.util.List;

public interface QnAService {

<<<<<<< Updated upstream
    List<QnAEntity> allout();
=======
    void insertQnA(QnAEntity qnaEntity);

    List<QnAEntity> allout();

    QnAEntity DetailById(Long qnaId);

    QnAEntity UpdateById(Long qnaId);

    void UpdateSave(QnAEntity qnaEntity);

    QnAEntity DeleteById(Long qnaId);

    void DeleteSave(Long qnaId);
>>>>>>> Stashed changes
}
