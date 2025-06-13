package com.example.PetLog.ItemUser;

import com.example.PetLog.Item.ItemEntity;
import com.example.PetLog.User.UserEntity;
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
@Table(name = "usertems")
public class ItemUserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "usertems_seq")
    @SequenceGenerator(
            name = "usertems_seq",
            sequenceName = "usertems_seq",
            allocationSize = 1,
            initialValue = 1
    )
    @Column(name = "usertem_id")
    Long usertemId;

    @Column(name = "usertem_equip")
    String usertemEquip;

    @ManyToOne
    @JoinColumn(name = "item_id", referencedColumnName = "item_id", insertable = false, updatable = false)
    ItemEntity item;  // ItemEntity와 연결 (item_id와 매핑)

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", insertable = false, updatable = false)
    UserEntity user;  // UserEntity와 연결 (user_id와 매핑)

    @Column(name = "item_id")
    Long itemId;

    @Column(name = "user_id")
    Long userId;

}
