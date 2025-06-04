package com.example.PetLog.Pet;

import com.example.PetLog.User.UserEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface PetService {

    void save(PetEntity petEntity);

    PetEntity detail(long petId);

    void update(PetEntity petEntity);

    void delete(Long petId);

    List<PetEntity> petOut(UserEntity loginUser);
}
