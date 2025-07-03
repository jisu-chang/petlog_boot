package com.example.PetLog.Point;

import com.example.PetLog.Snack.SnackEntity;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;

@Service
public class PointServiceImp implements PointService {
    @Autowired
    PointRepository pointRepository;

    @Override
    @Transactional
    public void deleteByUserId(Long userId) {
        List<PointEntity> posts = pointRepository.findByUser_UserId(userId);
        pointRepository.deleteByUser_UserId(userId);
    }
}
