package com.example.PetLog.Point;

import com.example.PetLog.User.UserEntity;
import org.springframework.stereotype.Service;

@Service
public interface PointService {

    void deleteByUserId(Long userId);
}
