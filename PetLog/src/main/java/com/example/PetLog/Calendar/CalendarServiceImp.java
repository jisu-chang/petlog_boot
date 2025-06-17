package com.example.PetLog.Calendar;

import com.example.PetLog.Diary.DiaryDTO;
import com.example.PetLog.Diary.DiaryEntity;
import com.example.PetLog.Diary.DiaryRepository;
import com.example.PetLog.Pet.PetDTO;
import com.example.PetLog.Pet.PetEntity;
import com.example.PetLog.Pet.PetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
