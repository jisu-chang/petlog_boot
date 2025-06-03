package com.example.PetLog.Diary;

import com.example.PetLog.Pet.PetEntity;
import com.example.PetLog.User.UserEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "diary")
@Builder
public class DiaryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "DIARY_SEQ")
    @SequenceGenerator(
            name = "DIARY_SEQ",
            sequenceName = "DIARY_SEQ",
            allocationSize = 1,
            initialValue = 1
    )
    @Column(name = "diary_id")
    long diaryId;

    @Column(name = "diary_title")
    String diaryTitle;

    @Column(name = "diary_date")
    LocalDate diaryDate;

    @Column(name = "diary_image")
    String diaryImage;

    @Column(name = "get_grape")
    int getGrape;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pet_id")
    PetEntity pet;

//    @Column(name = "pet_name")
//    String petName;


}
