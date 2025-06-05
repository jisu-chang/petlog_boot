package com.example.PetLog.Diary;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "diary")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
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
    private Long diaryId;

    @Column(name = "diary_title")
    private String diaryTitle;

    @Column(name = "diary_date")
    private LocalDate diaryDate;

    @Column(name = "diary_image")
    private String diaryImage;

    @Column(name = "diary_content")
    private String diaryContent;

    @Column(name = "get_grape")
    private int getGrape;

    @Column(name = "user_id")
    private Long userId;    // 직접 숫자 ID 필드로 변경

    @Column(name = "pet_id")
    private Long petId;     // 직접 숫자 ID 필드로 변경

}