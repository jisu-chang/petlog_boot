package com.example.PetLog.Quiz;

import com.example.PetLog.QuizResult.QuizResultRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

@Service
public class QuizServiceImp implements QuizService{

    @Autowired
    QuizRepository quizRepository;
    @Autowired
    QuizResultRepository quizResultRepository;

    @Override
    public void insertQuiz(QuizEntity quizEntity) {
        quizRepository.save(quizEntity);
    }

    @Override
    public List<QuizEntity> allout() {
        return quizRepository.findAll();
    }

    @Override
    public QuizEntity updateByQuizId(Long quizId) {
        return quizRepository.findById(quizId).orElse(null);
    }

    @Override
    public void updateSave(QuizEntity entity) {
        quizRepository.save(entity);
    }

    @Override
    public QuizEntity DeleteByQuizId(Long quizId) {
        return quizRepository.findById(quizId).orElse(null);
    }

    @Override
    public void deleteSave(Long quizId) {
        quizRepository.deleteById(quizId);
    }

    //유저 아이디로 풀지않은 퀴즈 랜덤 가져오기
    @Override
    public QuizDTO getRandomUnsolvedQuiz(Long userId) {
        // 유저가 푼 퀴즈 목록
        List<Long> solvedQuizIds = quizResultRepository.findSolvedQuizIdByUserIds(userId);

        List<QuizEntity> unsolvedQuiz;
        if (solvedQuizIds.isEmpty()) {
            unsolvedQuiz = quizRepository.findAll(); // 아무 것도 안 풀었으면 전체
        } else {
            unsolvedQuiz = quizRepository.findByQuizIdNotIn(solvedQuizIds); // 푼 건 제외
        }

        if (unsolvedQuiz.isEmpty()) return null; // 모든 퀴즈를 푼 경우

        // 랜덤으로 하나 뽑아서 DTO로 변환
        QuizEntity selected = unsolvedQuiz.get(new Random().nextInt(unsolvedQuiz.size()));
        return QuizDTO.fromEntity(selected);
    }

    @Override
    public QuizDTO getQuizById(Long quizId) {
        QuizEntity quiz = quizRepository.findById(quizId).orElseThrow(() -> new IllegalArgumentException("모든 퀴즈를 완료하였습니다."+quizId));
        return QuizDTO.fromEntity(quiz);
    }
}
