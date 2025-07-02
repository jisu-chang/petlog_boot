package com.example.PetLog.Calendar;

import com.example.PetLog.Community.CommunityEntity;
import com.example.PetLog.Diary.DiaryDTO;
import com.example.PetLog.Diary.DiaryEntity;
import com.example.PetLog.Diary.DiaryRepository;
import com.example.PetLog.Pet.PetDTO;
import com.example.PetLog.Pet.PetEntity;
import com.example.PetLog.Pet.PetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Service
public class CalendarServiceImp implements CalendarService {

    @Autowired
    PetRepository petRepository;

    @Autowired
    CalendarRepository calendarRepository;

    @Autowired
    DiaryRepository diaryRepository;

    @Override
    public List<PetDTO> getPets(Long userId) {
        List<PetEntity> entityList = petRepository.findByUser_UserId(userId);
        List<PetDTO> dtoList = new ArrayList<>();

        for (PetEntity entity : entityList) {
            PetDTO dto = new PetDTO();
            dto.setPetId(entity.getPetId());
            dto.setPetName(entity.getPetName());
            dto.setPetBog(entity.getPetBog());
            dto.setPetHbd(entity.getPetHbd());
            dto.setUserId(entity.getUser().getUserId());
            dto.setPetImgName(entity.getPetImg());
            dto.setPetNeuter(entity.getPetNeuter());
            dtoList.add(dto);
        }

        return dtoList;
    }

    @Override
    public void insertSchedule(CalendarDTO dto, Long userId) {
        CalendarEntity entity = CalendarEntity.builder()
                .calTitle(dto.getCalTitle())
                .calContent(dto.getCalContent())
                .calDate(dto.getCalDate())
                .userId(userId)
                .petId(dto.getPetId())
                .build();

        calendarRepository.save(entity);
    }

    @Override
    public void save(CalendarDTO dto) {
        CalendarEntity entity = dto.toEntity();
        calendarRepository.save(entity);
    }

    @Override
    public CalendarDTO calendar_detail(Long calId) {
        CalendarEntity entity = calendarRepository.findById(calId)
                .orElseThrow(() -> new RuntimeException("Not found calId: " + calId));
        return entity.toDTO(); // 엔티티에 toDTO() 메서드를 만들어 변환
    }

    @Override
    public void updateSchedule(CalendarDTO calendarDTO) {
        CalendarEntity calendarEntity = calendarDTO.toEntity();
        calendarRepository.save(calendarEntity);
    }

    @Override
    public void deleteSchedule(Long calId) {
        calendarRepository.deleteById(calId);
    }

    @Override
    @Transactional
    public void deleteByUserId(Long userId) {
        // 해당 유저의 게시글 불러오기
        List<CalendarEntity> posts = calendarRepository.findByUser_UserId(userId);
        // DB에서 게시글 삭제
        calendarRepository.deleteByUser_UserId(userId);
    }

    @Override
    public List<CalendarDTO> getCalList(Long userId, int year, int month, Long petId) {
        String yearStr = String.valueOf(year);
        String monthStr = String.format("%02d", month);

        List<CalendarEntity> entityList = calendarRepository.findCalendarListByMonth(userId, yearStr, monthStr, petId);
        List<CalendarDTO> dtoList = new ArrayList<>();

        for (CalendarEntity entity : entityList) {
            CalendarDTO dto = new CalendarDTO();
            dto.setCalId(entity.getCalId());
            dto.setCalTitle(entity.getCalTitle());
            dto.setCalContent(entity.getCalContent());
            dto.setCalDate(entity.getCalDate());
            dto.setUserId(entity.getUserId());
            dto.setPetId(entity.getPetId());
            dtoList.add(dto);
        }

        return dtoList;
    }

    @Override
    public List<DiaryDTO> getDiaryList(Long userId, int year, int month, Long petId) {
        List<DiaryDTO> dtoList = new ArrayList<>();

        String yearStr = String.valueOf(year);
        String monthStr = String.format("%02d", month); // 예: 01, 02, ..., 12

        List<DiaryEntity> entityList = diaryRepository.findDiaryByMonth(userId, yearStr, monthStr, petId);

        for (DiaryEntity entity : entityList) {
            DiaryDTO dto = new DiaryDTO();
            dto.setDiaryId(entity.getDiaryId());
            dto.setDiaryTitle(entity.getDiaryTitle());
            dto.setDiaryContent(entity.getDiaryContent());
            dto.setDiaryDate(entity.getDiaryDate());
            dto.setUserId(entity.getUserId());
            dto.setPetId(entity.getPetId());
            dtoList.add(dto);
        }

        return dtoList;
    }
}
