package com.example.PetLog.User;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Table(name = "user1")
public class UserEntity {
    @Id
    @Column(name = "user_id")
    Long userId;
    @Column(name = "user_login_id")
    String userLoginId;
    @Column
    String password;
    @Column
    String name;
    @Column
    String phone;
    @Column
    String email;
    @Column
    String profileimg;
    @Column
    String rank;   //int -> String 수정
    @Column(name = "user_role")
    String userRole;
    @Column(name = "grape_count")
    int grapeCount;

}