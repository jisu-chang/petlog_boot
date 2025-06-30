package com.example.PetLog.QnA;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QnAServiceImp implements QnAService{

    @Autowired
    QnARepository qnARepository;
    @Override
    public void insertQnA(QnAEntity qnaEntity) {
        qnARepository.save(qnaEntity);
    }

    @Override
    public List<QnAEntity> allout() {
        return qnARepository.findAll();
    }

    @Override
    public QnAEntity DetailById(Long qnaId) {
        return qnARepository.findById(qnaId).orElse(null);
    }

    @Override
    public QnAEntity UpdateById(Long qnaId) {
        return qnARepository.findById(qnaId).orElse(null);
    }

    @Override
    public void UpdateSave(QnAEntity qnaEntity) {
        qnARepository.save(qnaEntity);
    }

    @Override
    public QnAEntity DeleteById(Long qnaId) {
        return qnARepository.findById(qnaId).orElse(null);
    }

    @Override
    public void DeleteSave(Long qnaId) {
        qnARepository.deleteById(qnaId);
    }

    @Override
    public List<QnAEntity> findByUserId(Long userId) {
        return qnARepository.findByUserId(userId);
    }

}
