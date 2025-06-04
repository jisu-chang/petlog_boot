package com.example.PetLog.Pet;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface PetService {

    void save(PetEntity petEntity);

    List<PetEntity> petOut();

    PetEntity detail(long petId);

    void update(PetEntity petEntity);

    void delete(Long petId);
}
