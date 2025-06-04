package com.example.PetLog.Pet;

import com.example.PetLog.User.UserEntity;
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
    public List<PetEntity> petOut(UserEntity user) {
        return petRepository.findByUser(user); // user 기준으로 pet 조회
    }

    @Override
    public PetEntity detail(long petId) {
        return petRepository.findById(petId).orElse(null);
    }

    @Override
    public void update(PetEntity petEntity) {
        petRepository.save(petEntity);
    }

    @Override
    public void delete(Long petId) {
        petRepository.deleteById(petId);
    }


}
