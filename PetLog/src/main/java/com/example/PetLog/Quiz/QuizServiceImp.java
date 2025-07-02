package com.example.PetLog.Quiz;

import com.example.PetLog.QuizResult.QuizResultRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
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
    @Transactional
    public void deleteSave(Long quizId) {
        quizResultRepository.deleteByQuizId(quizId); //결과 먼저 삭제
        quizRepository.deleteById(quizId); //퀴즈 문제 삭제
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

    @Override
    public List<QuizDTO> getQuizListByIds(List<Long> ids) {
        return quizRepository.findByQuizIdIn(ids).stream()
                .map(entity -> {
                    QuizDTO dto = new QuizDTO();
                    dto.setQuizId(entity.getQuizId());
                    dto.setQuizQuestion(entity.getQuizQuestion());
                    dto.setQuizOption1(entity.getQuizOption1());
                    dto.setQuizOption2(entity.getQuizOption2());
                    dto.setQuizOption3(entity.getQuizOption3());
                    dto.setQuizOption4(entity.getQuizOption4());
                    dto.setQuizAnswer(entity.getQuizAnswer());
                    return dto;
                })
                .toList();
    }

    @Override
    public boolean checkAnswer(Long quizId, String selectedAnswer) {

        Optional<QuizEntity> optionalQuiz = quizRepository.findById(quizId);

        return optionalQuiz
                .map(quiz -> {
                    String actualAnswer = quiz.getQuizAnswer();

                    return actualAnswer.trim().equalsIgnoreCase(selectedAnswer.trim());
                })
                .orElse(false);
    }
}
