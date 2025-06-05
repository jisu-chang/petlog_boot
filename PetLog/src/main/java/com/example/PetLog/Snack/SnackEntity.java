package com.example.PetLog.Snack;

import com.example.PetLog.User.UserEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Table(name = "snack")
@SequenceGenerator(
        name = "snack_seq",
        sequenceName = "snack_seq",
        allocationSize = 1,
        initialValue = 1
)
public class SnackEntity {
    @Id
    @Column (name = "snack_id")
    @SequenceGenerator(
            name = "snack_seq",sequenceName = "snack_seq",
            allocationSize = 1, initialValue = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE,generator = "snack_seq")
    Long snackId;
    @Column (name = "snack_title")
    String snackTitle;
    @Column (name = "snack_recipe")
    String snackRecipe;
    @Column (name = "snack_image")
    String snackImage;
    @Column (name = "snack_date")
    LocalDate snackDate;
    @Column (name = "user_id")
    Long userId;
    @Column (name = "snack_readcnt")
    int snackReadcnt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", insertable = false, updatable = false)
    UserEntity user;
}
