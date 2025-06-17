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

    Long calId;
    String calTitle;
    String calContent;
    LocalDate calDate;

    Long userId;
    Long petId;
    String petName;

}
