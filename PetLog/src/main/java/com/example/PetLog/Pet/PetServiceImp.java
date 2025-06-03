package com.example.PetLog.Pet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PetServiceImp implements PetService {

    @Autowired
    PetRepository petRepository;

    @Override
    public void save(PetEntity petEntity) {
        petRepository.save(petEntity);
    }

    @Override
    public List<PetEntity> petOut() {
        return petRepository.findAll();
    }

    @Override
    public PetEntity detail(long petId) {
        return petRepository.findById(petId).orElse(null);
    }


}
