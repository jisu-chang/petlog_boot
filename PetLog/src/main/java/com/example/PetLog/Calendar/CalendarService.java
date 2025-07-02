package com.example.PetLog.Calendar;

import com.example.PetLog.Diary.DiaryDTO;
import com.example.PetLog.Pet.PetDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface CalendarService {

    List<CalendarDTO> getCalList(Long userId, int year, int month, Long petId);

    List<DiaryDTO> getDiaryList(Long userId, int currentYear, int currentMonth, Long petId);

    List<PetDTO> getPets(Long userId);

    void insertSchedule(CalendarDTO calendarDTO, Long userId);

    void save(CalendarDTO dto);

    CalendarDTO calendar_detail(Long calId);

    void updateSchedule(CalendarDTO calendarDTO);

    void deleteSchedule(Long calId);

    void deleteByUserId(Long userId);
}
