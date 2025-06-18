package com.example.PetLog.Calendar;

import com.example.PetLog.User.UserEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Builder
@Table(name = "calendar")
public class CalendarEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "cal_seq")
    @SequenceGenerator(
            name = "cal_seq",
            sequenceName = "cal_seq",
            allocationSize = 1,
            initialValue = 1)
    @Column(name = "cal_id")
    private Long calId;

    @Column(name = "cal_title")
    private String calTitle;

    @Column(name = "cal_content")
    private String calContent;

    @Column(name = "cal_date")
    private LocalDate calDate;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "pet_id")
    private Long petId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", insertable = false, updatable = false)
    private UserEntity user;


    public CalendarDTO toDTO() {
        return CalendarDTO.builder()
                .calId(calId)
                .calTitle(calTitle)
                .calContent(calContent)
                .calDate(calDate)
                .userId(userId)
                .petId(petId)
                .build();
    }
}
