package com.example.PetLog.Pet;

import com.example.PetLog.User.UserEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface PetService {

    void save(PetEntity petEntity);

    List<PetDTO> findPetsByUserId(Long userId); //from calendar

    PetEntity detail(long petId);

    void update(PetEntity petEntity);

    void delete(Long petId);

    List<PetEntity> petOut(UserEntity loginUser);

    List<PetEntity> findByUserId(Long userId);

    PetEntity findByPetId(Long petId);

    void deleteByUserId(Long userId);

    //List<PetDTO> findPetsByUserId(Long userId);
}
