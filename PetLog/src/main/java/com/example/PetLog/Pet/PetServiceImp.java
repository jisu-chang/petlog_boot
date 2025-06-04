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
    public List<PetDTO> findPetById(Long userId) { //diary쓸때 반려동물 선택하기
        // 1) userId로 petEntity 리스트 조회 (예: petRepository.findByUserId(userId))
        List<PetEntity> petEntities = petRepository.findByUserUserId(userId);

        // 2) petEntity 리스트 → petDTO 리스트 변환
        List<PetDTO> petDTOList = new ArrayList<>();
        for (PetEntity pet : petEntities) {
            PetDTO dto = new PetDTO();
            dto.setPetId(pet.getPetId());
            dto.setPetName(pet.getPetName());
            // 필요한 다른 필드도 세팅
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
