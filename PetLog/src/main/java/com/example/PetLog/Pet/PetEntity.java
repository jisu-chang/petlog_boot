package com.example.PetLog.Pet;

import com.example.PetLog.User.UserEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "pet")
@Builder
public class PetEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "pet_seq")
    @SequenceGenerator(
            name = "pet_seq",
            sequenceName = "pet_seq",
            allocationSize = 1,
            initialValue = 1
    )
    @Column(name = "pet_id")
    long petId;

    @Column(name = "pet_name")
    String petName;

    @Column(name = "pet_bog")
    String petBog;

    @Column(name = "pet_hbd")
    LocalDate petHbd;

    @ManyToOne
    @JoinColumn(name="user_id", nullable=false)
    UserEntity user;

    @Column(name = "pet_img")
    String petImg;

    @Column(name = "pet_neuter")
    String petNeuter;

    public String getPetImg() {
        return petImg;
    }


}
