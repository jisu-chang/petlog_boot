package com.example.PetLog.QuizResult;

import com.example.PetLog.Quiz.QuizEntity;
import com.example.PetLog.Quiz.QuizRepository;
import com.example.PetLog.User.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
public class QuizResultServiceImp implements QuizResultService{

    @Autowired
    QuizResultRepository quizResultRepository;
    @Autowired
    QuizRepository quizRepository;

    @Override
    public void saveResult(QuizResultDTO dto, int quizAnswer) {
        // 1. 퀴즈 정답 조회
        QuizEntity quiz = quizRepository.findById(dto.getQuizId())
                .orElseThrow(() -> new IllegalArgumentException("퀴즈 없음"));

        // 2. 선택한 보기 번호로 보기 문자열 가져오기
        String userSelectedAnswer = switch (quizAnswer) {
            case 1 -> quiz.getQuizOption1();
            case 2 -> quiz.getQuizOption2();
            case 3 -> quiz.getQuizOption3();
            case 4 -> quiz.getQuizOption4();
            default -> null;
        };

        // 3. 정답 비교
        int score = quiz.getQuizAnswer().equals(userSelectedAnswer) ? 1 : 0;
        dto.setResultScore(score);

        // 4. 중복 저장 방지
        Integer count = quizResultRepository.countByUserIdAndQuizId(dto.getUserId(), dto.getQuizId());
        if (count != null && count > 0) {
            throw new IllegalStateException("이미 푼 퀴즈입니다.");
        }

        // 5. 저장
        QuizResultEntity entity = new QuizResultEntity();
        entity.setUserId(dto.getUserId());
        entity.setQuizId(dto.getQuizId());
        entity.setResultScore(score);
        entity.setResultTime(dto.getResultTime());
        entity.setGetGrape(dto.getGetGrape());
        entity.setQuiz(quiz); // 연관된 퀴즈 정보 저장
        System.out.println("선택한 번호: " + quizAnswer);
        System.out.println("사용자 보기 값: " + quizAnswer);
        System.out.println("정답 보기 값: " + quiz.getQuizAnswer());
        quizResultRepository.save(entity);
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
            dto.setUserLoginId((String) row[7]);

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
