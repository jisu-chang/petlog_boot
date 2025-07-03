package com.example.PetLog.QnA;

import com.example.PetLog.Calendar.CalendarEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        List<QnAEntity> list = qnARepository.findAll(); // 또는 커스텀 메서드

        // 정렬: qnaId 내림차순 (최신 글 먼저)
        list.sort((a, b) -> Long.compare(b.getQnaId(), a.getQnaId()));

        return list;
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
        List<QnAEntity> list = qnARepository.findByUserId(userId);

        list.sort((a, b) -> Long.compare(b.getQnaId(), a.getQnaId()));

        return list;
    }

    @Override
    @Transactional
    public void deleteByUserId(Long userId) {
        // 해당 유저의 게시글 불러오기
        List<QnAEntity> posts = qnARepository.findByUser_UserId(userId);
        // DB에서 게시글 삭제
        qnARepository.deleteByUser_UserId(userId);
    }

    //전체 QnA page
    @Override
    public List<QnAEntity> getQnAPage(int offset, int limit) {
        return qnARepository.findPagedQnA(offset + limit, offset + 1); // Oracle 방식
    }

    @Override
    public int getTotalQnACount() {
        return qnARepository.countAllQnA();
    }

    //My QnA page
    @Override
    public List<QnAEntity> getUserQnAPage(Long userId, int offset, int limit) {
        int startRow = offset + 1;
        int endRow = offset + limit;
        return qnARepository.findPagedUserQnA(userId, endRow, startRow);
    }

    @Override
    public int getTotalUserQnACount(Long userId) {
        return qnARepository.countUserQnA(userId);
    }
}
