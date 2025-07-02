package com.example.PetLog.Diary;

import com.example.PetLog.User.UserEntity;
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
    Long diaryId;

    @Column(name = "diary_title")
    String diaryTitle;

    @Column(name = "diary_date")
    LocalDate diaryDate;

    @Column(name = "diary_image")
    String diaryImage;

    @Column(name = "diary_content")
    String diaryContent;

    @Column(name = "get_grape")
    int getGrape;

    @Column(name = "user_id")
    Long userId;    // 직접 숫자 ID 필드로 변경

    @Column(name = "pet_id")
    Long petId;     // 직접 숫자 ID 필드로 변경

    @Transient
    private String petName;

    //지수 추가
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", insertable = false, updatable = false)
    UserEntity user;

}