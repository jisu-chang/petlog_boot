package com.example.PetLog.Point;

import com.example.PetLog.User.UserEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "grape_point")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PointEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "grape_point_seq")
    @SequenceGenerator(
            name = "grape_point_seq",
            sequenceName = "grape_point_seq",
            allocationSize = 1,
            initialValue = 1
    )
    @Column(name = "point_id")
    Long pointId;

    @Column(name = "point_action")
    String pointAction;

    @Column(name = "point_action_id")
    Long pointActionId;

    @Column(name = "point_earned_grapes")
    Long pointEarnedGrapes;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    UserEntity user;
}
