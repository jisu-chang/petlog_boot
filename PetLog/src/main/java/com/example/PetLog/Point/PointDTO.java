package com.example.PetLog.Point;

import com.example.PetLog.User.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PointDTO {

    Long pointId;
    Long userId;
    String pointAction;
    Long pointActionId;
    Long pointEarnedGrapes;

    public PointEntity entity(UserEntity user) {
        return PointEntity.builder()
                .pointId(pointId)
                .pointAction(pointAction)
                .pointActionId(pointActionId)
                .pointEarnedGrapes(pointEarnedGrapes)
                .user(user)
                .build();
    }



}
