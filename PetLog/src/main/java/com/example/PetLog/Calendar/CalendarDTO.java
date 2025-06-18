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
    private String petName;

    public CalendarEntity toEntity() {
        return CalendarEntity.builder()
                .calId(calId)
                .userId(userId)
                .petId(petId)
                .calTitle(calTitle)
                .calContent(calContent)
                .calDate(calDate)
                .build();
    }

}