package com.example.PetLog.Calendar;

import com.example.PetLog.Diary.DiaryDTO;
import com.example.PetLog.Pet.PetDTO;
import com.example.PetLog.Pet.PetEntity;
import com.example.PetLog.Pet.PetService;
import com.example.PetLog.User.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.List;

@Controller
public class CalendarController {

    @Autowired
    CalendarService calendarService;

    @Autowired
    PetService petService;

    @Autowired
    UserService userService;

    @GetMapping(value = "/Calendar/CalendarView")
    public String cal(@RequestParam(value = "pet_id", required = false) Long petId,
                      @RequestParam(value = "year", required = false) Integer year,
                      @RequestParam(value = "month", required = false) Integer month,
                      Principal principal,
                      Model mo) {

        if (principal == null) {
            return "redirect:/login";
        }

        String loginId = principal.getName();
        Long userId = userService.findUserIdByLoginId(loginId); // ✅ 실제 로그인된 유저 ID

        List<PetDTO> petList = calendarService.getPets(userId);
        mo.addAttribute("petlist", petList);

        if (petList.isEmpty()) return "redirect:/Calendar/CalendarInput?error=no_pet";

        if (petId == null) petId = petList.get(0).getPetId();

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
        mo.addAttribute("prevYear", (currentMonth == 1) ? currentYear - 1 : currentYear);
        mo.addAttribute("prevMonth", (currentMonth == 1) ? 12 : currentMonth - 1);
        mo.addAttribute("nextYear", (currentMonth == 12) ? currentYear + 1 : currentYear);
        mo.addAttribute("nextMonth", (currentMonth == 12) ? 1 : currentMonth + 1);

        List<CalendarDTO> calList = calendarService.getCalList(userId, currentYear, currentMonth, petId);
        List<DiaryDTO> diaryList = calendarService.getDiaryList(userId, currentYear, currentMonth, petId);

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

    @GetMapping(value = "/Calendar/CalendarInput")
    public String cal2(HttpSession session, Model model) {
        // 🔒 로그인 확인 코드 (나중에 사용 예정)
//        Object sessionUserId = session.getAttribute("user_id");
//        if (sessionUserId == null) return "redirect:/User/Login";
//        Long userId = Long.valueOf(sessionUserId.toString());

        Long userId = 1L; // ← 임시 계정 ID

        List<PetEntity> petlist = petService.findByUserId(userId); // 반드시 로그인한 사용자의 펫 리스트
        model.addAttribute("petlist", petlist);
        return "Calendar/CalendarInput";
    }

    @PostMapping(value = "/Calendar/CalendarInput")
    public String cal3(@ModelAttribute CalendarDTO calendarDTO, HttpSession session) {
        // 🔒 로그인 확인 코드 (나중에 사용 예정)
//        Object sessionUserId = session.getAttribute("user_id");
//        if (sessionUserId == null) return "redirect:/User/Login";
//        Long userId = Long.valueOf(sessionUserId.toString());

        Long userId = 1L;

        calendarService.insertSchedule(calendarDTO, userId);
        return "redirect:/Calendar/CalendarView";
    }

    @PostMapping(value = "/Calendar/CalendarSave")
    public String saveSchedule(HttpSession session,
                               @RequestParam("pet_id") Long petId,
                               @RequestParam("cal_title") String calTitle,
                               @RequestParam("cal_content") String calContent,
                               @RequestParam("cal_date") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate calDate) {

        Long userId = (Long) session.getAttribute("userId");
        String userLoginId = (String) session.getAttribute("userLoginId");

        if (userId == null || userLoginId == null) {
            return "redirect:/login?error=login_required";
        }

        CalendarDTO dto = new CalendarDTO();
        dto.setPetId(petId);
        dto.setCalTitle(calTitle);
        dto.setCalContent(calContent);
        dto.setCalDate(calDate);
        dto.setUserId(userId);

        calendarService.save(dto);

        String year = String.valueOf(calDate.getYear());
        String month = String.format("%02d", calDate.getMonthValue());

        return "redirect:/Calendar/CalendarView?pet_id=" + petId + "&year=" + year + "&month=" + month;
    }



}

