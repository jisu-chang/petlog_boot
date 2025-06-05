package com.example.PetLog.Snack;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SnackServiceImp implements SnackService {

    @Autowired
    SnackRepository snackRepository;

    @Override
    public void save(SnackEntity snackEntity) {
        snackRepository.save(snackEntity);
    }
}
