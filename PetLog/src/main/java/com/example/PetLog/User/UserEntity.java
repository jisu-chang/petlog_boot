package com.example.PetLog.User;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Table(name = "user1")
@SequenceGenerator(
        name = "user_id",
        sequenceName = "user_id_seq",
        allocationSize = 1,
        initialValue = 1
)
public class UserEntity {
    @Id
    @Column(name = "user_id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE,generator = "user_id")
    Long userId;
    @Column(name = "user_login_id", nullable = false)
    String userLoginId;
    @Column
    String password;
    @Column
    String name;
    @Column
    String phone;
    @Column
    String email;
    @Column(name = "profileimg")
    String profileimg;
    @Column
    String rank;   //int -> String 수정
    @Column(name = "user_role")
    String userRole;
    @Column(name = "grape_count")
    int grapeCount;

    @Transient
    String profileimgName;

}
