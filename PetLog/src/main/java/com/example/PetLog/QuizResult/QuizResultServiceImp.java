package com.example.PetLog.QuizResult;

import com.example.PetLog.Calendar.CalendarEntity;
import com.example.PetLog.Quiz.QuizEntity;
import com.example.PetLog.Quiz.QuizRepository;
import com.example.PetLog.Quiz.QuizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;


@Service
public class QuizResultServiceImp implements QuizResultService{

    @Autowired
    QuizResultRepository quizResultRepository;
    @Autowired
    QuizRepository quizRepository;
    @Autowired
    QuizService quizService;

    @Override
    public void saveResult(QuizResultDTO dto, String userAnswer) {
        QuizEntity quiz = quizRepository.findById(dto.getQuizId())
                .orElseThrow(() -> new IllegalArgumentException("퀴즈 없음"));

        // 퀴즈의 정답 (DB 저장된 값)
        String correctAnswer = quiz.getQuizAnswer(); // e.g. "3"

        // 사용자 응답과 정답 비교
        int score = correctAnswer.equals(userAnswer) ? 1 : 0;

        // DTO에 사용자 응답과 점수 저장 (userAnswer는 화면 출력용임)
        dto.setUserAnswer(userAnswer);
        dto.setResultScore(score);
        dto.setQuizAnswer(correctAnswer); // 정답도 DTO에 같이 담아야 화면 비교

        // 이미 푼 퀴즈인지 확인
        Integer count = quizResultRepository.countByUserIdAndQuizId(dto.getUserId(), dto.getQuizId());
        if (count != null && count > 0) {
            throw new IllegalStateException("이미 푼 퀴즈입니다.");
        }

        // 정답일 경우 순위 계산
        int rank = 0;
        if (score == 1) {
            rank = calculateUserRank(dto.getQuizId(), dto.getResultTime());
        }

        // 엔티티로 저장 (userAnswer는 저장 안 함)
        QuizResultEntity entity = new QuizResultEntity();
        entity.setUserId(dto.getUserId());
        entity.setQuizId(dto.getQuizId());
        entity.setResultScore(score);
        entity.setResultTime(dto.getResultTime());
        entity.setGetGrape(dto.getGetGrape());
        entity.setUserAnswer(userAnswer);
        entity.setResultRank(rank);
        entity.setQuiz(quiz); // quiz 안에 정답이 포함돼 있음

        quizResultRepository.save(entity);
    }

    public List<QuizResultDTO> getUserQuizList(Long userId) {
        List<QuizResultEntity> entityList = quizResultRepository.findByUserId(userId);
        List<QuizResultDTO> dtoList = new ArrayList<>();

        for (QuizResultEntity entity : entityList) {
            QuizResultDTO dto = new QuizResultDTO();
            dto.setQuizId(entity.getQuizId());
            dto.setResultScore(entity.getResultScore());
            dto.setResultTime(entity.getResultTime());
            dto.setGetGrape(entity.getGetGrape());
            dto.setUserAnswer(entity.getUserAnswer());

            dtoList.add(dto);
        }
        return dtoList;
    }

    @Override
    @Transactional
    public void deleteByUserId(Long userId) {
        // 해당 유저의 게시글 불러오기
        List<QuizResultEntity> posts = quizResultRepository.findByUser_UserId(userId);
        // DB에서 게시글 삭제
        quizResultRepository.deleteByUserId(userId);
    }

    private int calculateUserRank(Long quizId, int resultTime) {
        return quizResultRepository.countRankByQuizIdAndFasterTime(quizId,resultTime) + 1;
    }

    //최신 결과 가져오기
    @Override
    public QuizResultEntity getLatestResultByUser(Long userId) {
        return quizResultRepository.findTopByUserIdOrderByResultIdDesc(userId);
    }

    //풀이시간 빠른 순으로 top10 가져오기
    @Override
    public List<QuizResultDTO> getTop10ByQuizId(Long userId, Long quizId) {
        List<Object[]> rawResults = quizResultRepository.findTop10RawDataByQuizId(quizId);
        List<QuizResultDTO> top10DTOs = new ArrayList<>();

        int rank = 1; // 순위 계산을 위한 변수 (1위부터 시작)
        for (Object[] row : rawResults) {
            QuizResultDTO dto = new QuizResultDTO();

            dto.setResultId(((Number) row[0]).longValue());
            dto.setResultScore(((Number) row[1]).intValue());
            dto.setResultRank(((Number) row[2]).intValue());
            dto.setResultTime(((Number) row[3]).intValue());
            dto.setUserId(((Number) row[4]).longValue());
            dto.setQuizId(((Number) row[5]).longValue());
            dto.setGetGrape(((Number) row[6]).intValue());
            dto.setUserAnswer((String) row[7]);
            dto.setUserLoginId((String) row[8]);

            // 순위 설정
            dto.setResultRank(rank++);
            top10DTOs.add(dto);
        }
        return top10DTOs;
    }

    @Override
    public QuizResultDTO getLatestResultDtoByUser(Long userId) {
        QuizResultEntity entity = getLatestResultByUser(userId); // 기존 서비스 메서드 호출
        if (entity == null) {
            return null;
        }

        QuizResultDTO dto = new QuizResultDTO();
        dto.setResultId(entity.getResultId());
        dto.setResultScore(entity.getResultScore());
        dto.setResultTime(entity.getResultTime());
        dto.setGetGrape(entity.getGetGrape());
        dto.setUserId(entity.getUserId());
        dto.setQuizId(entity.getQuizId());
        dto.setResultRank(entity.getResultRank()); // result_rank 컬럼이 DB에 있으므로 이 필드 사용

        // QuizEntity 정보 DTO에 설정
        // entity.getQuiz()가 null이 아닌지 확인하고 데이터를 복사합니다.
        if (entity.getQuiz() != null) {
            dto.setQuizAnswer(entity.getQuiz().getQuizAnswer());
            dto.setQuizOption1(entity.getQuiz().getQuizOption1());
            dto.setQuizOption2(entity.getQuiz().getQuizOption2());
            dto.setQuizOption3(entity.getQuiz().getQuizOption3());
            dto.setQuizOption4(entity.getQuiz().getQuizOption4());
        }

        // UserEntity 정보 DTO에 설정
        if (entity.getUser() != null) {
            dto.setUser(entity.getUser()); // DTO에 UserEntity 객체 그대로 추가 (필요에 따라 DTO로 변환)
        }
        return dto;
    }
}
