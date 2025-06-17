package com.example.PetLog.Calendar;

import com.example.PetLog.Diary.DiaryDTO;
import com.example.PetLog.Pet.PetDTO;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Calendar;
import java.util.Collections;
import java.util.List;

@Controller
public class CalendarController {

    @Autowired
    CalendarService calendarService;

    @GetMapping("/Calendar/CalendarView")
    public String cal(@RequestParam(value = "pet_id", required = false) Long petId,
                      @RequestParam(value = "year", required = false) Integer year,
                      @RequestParam(value = "month", required = false) Integer month,
                      HttpSession session, Model mo) {

        Integer userId = (Integer) session.getAttribute("user_id");

        List<PetDTO> petList = (userId != null) ? calendarService.getPets(userId.longValue()) : Collections.emptyList();
        mo.addAttribute("petlist", petList);

        if (petId == null) {
            if (!petList.isEmpty()) {
                petId = petList.get(0).getPetId();
            } else {
                return "redirect:/Calendar/CalendarInput?error=no_pet";
            }
        }

        // 날짜 설정
        Calendar cal = Calendar.getInstance();
        if (year != null && month != null) {
            cal.set(Calendar.YEAR, year);
            cal.set(Calendar.MONTH, month - 1);
        }
        int currentYear = cal.get(Calendar.YEAR);
        int currentMonth = cal.get(Calendar.MONTH) + 1;

        mo.addAttribute("current_year", currentYear);
        mo.addAttribute("current_month", currentMonth);
        mo.addAttribute("pet_id", petId);

        // 이전/다음 달 계산
        int prevYear = (currentMonth == 1) ? currentYear - 1 : currentYear;
        int prevMonth = (currentMonth == 1) ? 12 : currentMonth - 1;
        int nextYear = (currentMonth == 12) ? currentYear + 1 : currentYear;
        int nextMonth = (currentMonth == 12) ? 1 : currentMonth + 1;

        mo.addAttribute("prevYear", prevYear);
        mo.addAttribute("prevMonth", prevMonth);
        mo.addAttribute("nextYear", nextYear);
        mo.addAttribute("nextMonth", nextMonth);

        // 데이터 가져오기 (userId 없으면 빈 리스트)
        List<CalendarDTO> calList = (userId != null)
                ? calendarService.getCalList(userId.longValue(), currentYear, currentMonth, petId)
                : Collections.emptyList();

        List<DiaryDTO> diaryList = (userId != null)
                ? calendarService.getDiaryList(userId.longValue(), currentYear, currentMonth, petId)
                : Collections.emptyList();

        // 달력 HTML 생성
        StringBuilder html = new StringBuilder();
        cal.set(Calendar.DAY_OF_MONTH, 1);
        int startDay = cal.get(Calendar.DAY_OF_WEEK);
        int lastDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        int count = 1;

        for (int i = 1; i <= 35; i++) {
            if (i < startDay || count > lastDay) {
                html.append("<td class='calendar-cell'><div class='cell-content'></div></td>");
            } else {
                Calendar today = Calendar.getInstance();
                boolean isToday = (currentYear == today.get(Calendar.YEAR)
                        && currentMonth == (today.get(Calendar.MONTH) + 1)
                        && count == today.get(Calendar.DAY_OF_MONTH));
                String dayClass = (i % 7 == 1) ? "sun" : (i % 7 == 0) ? "sat" : "weekday";
                String todayClass = isToday ? " today" : "";

                html.append("<td class='calendar-cell'><div class='cell-content'>")
                        .append("<span class='date-number ").append(dayClass).append(todayClass).append("'>")
                        .append(count).append(isToday ? "<span class='today-label'> today</span>" : "")
                        .append("</span>");

                for (CalendarDTO e : calList) {
                    if (e.getCalDate().getDayOfMonth() == count) {
                        html.append("<div class='schedule-item'>📌 <a href='calendar_detail?cal_id=")
                                .append(e.getCalId()).append("'>")
                                .append(e.getCalTitle()).append("</a></div>");
                    }
                }

                for (DiaryDTO d : diaryList) {
                    if (d.getDiaryDate().getDayOfMonth() == count) {
                        html.append("<div class='schedule-item' style='background-color:#e0f7fa;'>📓 <a href='diary_detail?diary_id=")
                                .append(d.getDiaryId()).append("'>")
                                .append(d.getDiaryTitle()).append("</a></div>");
                    }
                }

                html.append("</div></td>");
                count++;
            }

            if (i % 7 == 0) {
                html.append("</tr><tr>");
            }
        }

        mo.addAttribute("calendarHtml", html.toString());
        return "Calendar/CalendarView";
    }

    @GetMapping("/Calendar/CalendarInput")
    public String cal2() {
        return "Calendar/CalendarInput";
    }

    @PostMapping("/Calendar/CalendarInput")
    public String cal3() {
        return "Calendar/CalendarInput";
    }
}