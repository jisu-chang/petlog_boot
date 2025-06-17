package com.example.PetLog.Calendar;

import com.example.PetLog.User.UserEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.print.attribute.standard.MediaSize;
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
    Long calId;

    @Column(name = "cal_title")
    String calTitle;

    @Column(name = "cal_content")
    String calContent;

    @Column(name = "cal_date")
    LocalDate calDate;

    @Column(name = "user_id")
    Long userId;

    @Column(name = "pet_id")
    Long petId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", insertable = false, updatable = false)
    UserEntity user;

}
