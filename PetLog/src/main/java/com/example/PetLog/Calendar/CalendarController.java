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
import java.util.Objects;

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
                      HttpSession session,
                      Model mo) {

        Long userId = (Long) session.getAttribute("userId");
        String userLoginId = (String) session.getAttribute("userLoginId");

        if (userId == null || userLoginId == null) {
            return "redirect:/login";
        }
        List<PetDTO> petList = calendarService.getPets(userId);
        mo.addAttribute("petlist", petList);

        if (petList.isEmpty()) return "redirect:/Calendar/CalendarInput?error=no_pet";

        if (petId == null) petId = petList.get(0).getPetId();

        String selectedPetName = "";
        for (PetDTO pet : petList) {
            if (Objects.equals(pet.getPetId(), petId)) {
                selectedPetName = pet.getPetName();
                break;
            }
        }
        mo.addAttribute("selectedPetName", selectedPetName);

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

        html.append("<tr>");

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
                        .append(count);
                if (isToday) {
                    html.append("<span class='today-label'> today</span>");
                }
                html.append("</span>");

                for (CalendarDTO e : calList) {
                    if (e.getCalDate().getDayOfMonth() == count) {
                        html.append("<a href='/Calendar/CalendarDetail?calId=").append(e.getCalId()).append("' class='schedule-item' style='background-color:#ffe0ec;'>")
                                .append("📌")
                                .append("</a>");
                    }
                }

                for (DiaryDTO d : diaryList) {
                    if (d.getDiaryDate().getDayOfMonth() == count) {
                        html.append("<a href='/Diary/DiaryDetail?diaryId=").append(d.getDiaryId()).append("' class='schedule-item' style='background-color:#e0f7fa;'>")
                                .append("📓")
                                .append("</a>");
                    }
                }

                html.append("</div></td>");
                count++;
            }

            if (i % 7 == 0 && i < 35) {
                html.append("</tr><tr>");
            }
        }
        html.append("</tr>");

        mo.addAttribute("calendarHtml", html.toString());
        return "Calendar/CalendarView";
    }

    @GetMapping(value = "/Calendar/CalendarInput")
    public String cal2(Model model, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        String userLoginId = (String) session.getAttribute("userLoginId");

        if (userId == null || userLoginId == null) {
            return "redirect:/login";
        }

        List<PetEntity> petlist = petService.findByUserId(userId);
        model.addAttribute("petlist", petlist);
        return "Calendar/CalendarInput";
    }

    @PostMapping(value = "/Calendar/CalendarInput")
    public String cal3(@ModelAttribute CalendarDTO calendarDTO, Principal principal) {

        if (principal == null) {
            return "redirect:/login";
        }
        String loginId = principal.getName();
        Long userId = userService.findUserIdByLoginId(loginId);

        calendarDTO.setUserId(userId);
        calendarService.insertSchedule(calendarDTO, userId);

        Long petId = calendarDTO.getPetId();
        LocalDate calDate = calendarDTO.getCalDate();
        String year = String.valueOf(calDate.getYear());
        String month = String.format("%02d", calDate.getMonthValue());

        return "redirect:/Calendar/CalendarView?pet_id=" + petId + "&year=" + year + "&month=" + month;
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

    @GetMapping(value = "/Calendar/CalendarDetail")
    public String detail(@RequestParam("calId") Long calId,
                         HttpSession session,
                         Model model) {

        Long userId = (Long) session.getAttribute("userId");
        String userLoginId = (String) session.getAttribute("userLoginId");

        if (userId == null || userLoginId == null) {
            return "redirect:/login";
        }

        CalendarDTO cdto = calendarService.calendar_detail(calId);

        if (cdto == null || !cdto.getUserId().equals(userId)) {
            return "redirect:/Calendar/CalendarView?error=not_found_or_unauthorized";
        }

        if (cdto.getPetId() != null) {
            PetEntity pet = petService.findByPetId(cdto.getPetId());
            if (pet != null) {
                cdto.setPetName(pet.getPetName());
            }
        }

        model.addAttribute("cdto", cdto);
        model.addAttribute("current_year", cdto.getCalDate().getYear());
        model.addAttribute("current_month", cdto.getCalDate().getMonthValue());
        model.addAttribute("pet_id", cdto.getPetId());

        return "Calendar/CalendarDetail";
    }

    @GetMapping(value = "/Calendar/CalendarUpdate")
    public String showUpdateForm(@RequestParam("cal_id") Long calId,
                                 @RequestParam(value = "year", required = false) Integer year,
                                 @RequestParam(value = "month", required = false) Integer month,
                                 @RequestParam(value = "pet_id", required = false) Long petIdFromRequest,
                                 HttpSession session,
                                 Model model) {

        Long userId = (Long) session.getAttribute("userId");
        String userLoginId = (String) session.getAttribute("userLoginId");

        if (userId == null || userLoginId == null) {
            return "redirect:/login";
        }
        CalendarDTO cdto = calendarService.calendar_detail(calId);

        if (cdto == null || !cdto.getUserId().equals(userId)) {
            return "redirect:/Calendar/CalendarView?error=calendar_not_found_or_unauthorized";
        }

        if (cdto.getPetId() != null) {
            PetEntity pet = petService.findByPetId(cdto.getPetId());
            if (pet != null) {
                cdto.setPetName(pet.getPetName());
            }
        }

        List<PetDTO> petList = calendarService.getPets(userId);
        model.addAttribute("petlist", petList);
        model.addAttribute("cdto", cdto);

        if (year != null && month != null) {
            model.addAttribute("current_year", year);
            model.addAttribute("current_month", month);
        } else {
            model.addAttribute("current_year", cdto.getCalDate().getYear());
            model.addAttribute("current_month", cdto.getCalDate().getMonthValue());
        }

        model.addAttribute("pet_id", cdto.getPetId());

        return "Calendar/CalendarUpdate";
    }

    @PostMapping(value = "/CalendarUpdateSave")
    public String updateScheduleSave(@ModelAttribute CalendarDTO calendarDTO,
                                     @RequestParam("year") Integer year,
                                     @RequestParam("month") Integer month,
                                     HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        String userLoginId = (String) session.getAttribute("userLoginId");

        if (userId == null || userLoginId == null) {
            return "redirect:/login";
        }

        calendarDTO.setUserId(userId);

        calendarService.updateSchedule(calendarDTO);

        return "redirect:/Calendar/CalendarView?year=" + year + "&month=" + month + "&pet_id=" + calendarDTO.getPetId();
    }

    @GetMapping(value = "/Calendar/CalendarDelete")
    public String showDeleteConfirmation(@RequestParam("calId") Long calId,
                                         @RequestParam(value = "year", required = false) Integer year,
                                         @RequestParam(value = "month", required = false) Integer month,
                                         @RequestParam(value = "petId", required = false) Long petId,
                                         Principal principal,
                                         Model model) {

        if (principal == null) {
            return "redirect:/login?error=login_required";
        }
        String loginId = principal.getName();
        Long userId = userService.findUserIdByLoginId(loginId);

        CalendarDTO cdto = calendarService.calendar_detail(calId);

        if (cdto == null || !cdto.getUserId().equals(userId)) {
            return "redirect:/Calendar/CalendarView?error=calendar_not_found_or_unauthorized_delete";
        }

        if (cdto.getPetId() != null) {
            PetEntity pet = petService.findByPetId(cdto.getPetId());
            if (pet != null) {
                cdto.setPetName(pet.getPetName());
            }
        }

        model.addAttribute("cdto", cdto);
        model.addAttribute("current_year", year != null ? year : cdto.getCalDate().getYear());
        model.addAttribute("current_month", month != null ? month : cdto.getCalDate().getMonthValue());
        model.addAttribute("pet_id", petId != null ? petId : cdto.getPetId());

        return "Calendar/CalendarDelete";
    }

    @PostMapping(value = "/Calendar/delete")
    public String deleteSchedule(@RequestParam("calId") Long calId,
                                 @RequestParam(value = "year", required = false) Integer year,
                                 @RequestParam(value = "month", required = false) Integer month,
                                 @RequestParam(value = "petId", required = false) Long petId,
                                 HttpSession session) {

        Long userId = (Long) session.getAttribute("userId");
        String userLoginId = (String) session.getAttribute("userLoginId");

        if (userId == null || userLoginId == null) {
            return "redirect:/login";
        }

        CalendarDTO cdto = calendarService.calendar_detail(calId);
        if (cdto == null || !cdto.getUserId().equals(userId)) {
            return "redirect:/Calendar/CalendarView?error=unauthorized_delete_attempt";
        }

        calendarService.deleteSchedule(calId);

        return "redirect:/Calendar/CalendarView?year=" + (year != null ? year : cdto.getCalDate().getYear())
                + "&month=" + (month != null ? month : cdto.getCalDate().getMonthValue())
                + "&pet_id=" + (petId != null ? petId : cdto.getPetId());
    }
}