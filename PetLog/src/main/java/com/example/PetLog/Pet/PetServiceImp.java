package com.example.PetLog.Pet;

import com.example.PetLog.User.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
    public List<PetEntity> findByUserId(Long userId) {
        return petRepository.findByUserUserId(userId);
    }

    @Override
    public List<PetDTO> findPetsByUserId(Long userId) {
        List<PetEntity> petEntities = petRepository.findByUserUserId(userId.longValue());

        List<PetDTO> petDTOList = new ArrayList<>();
        for (PetEntity pet : petEntities) {
            PetDTO dto = new PetDTO();
            dto.setPetId(pet.getPetId());
            dto.setPetName(pet.getPetName());
            // 필요 시 추가 필드 설정
            petDTOList.add(dto);
        }
        return petDTOList;
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
