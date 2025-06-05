package com.example.PetLog.Snack;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface SnackService {

    void save(SnackEntity snackEntity);
}
