package com.example.PetLog.Calendar;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class CalendarDTO {

    private Long calId;
    private String calTitle;
    private String calContent;
    private LocalDate calDate;

    private Long userId;
    private Long petId;
    private String petName; // 필요 없으면 생략 가능
}
