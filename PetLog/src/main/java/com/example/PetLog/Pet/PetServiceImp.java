package com.example.PetLog.Pet;

import com.example.PetLog.Snack.SnackEntity;
import com.example.PetLog.User.UserEntity;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Service
public class PetServiceImp implements PetService {

    @Autowired
    PetRepository petRepository;
    String path = "C:/petlog-uploads/petprofile";

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
    public PetEntity findByPetId(Long petId) {
        return petRepository.findById(petId).orElse(null);
    }

    @Override
    @Transactional
    public void deleteByUserId(Long userId) {
        // 해당 유저의 게시글 불러오기
        List<PetEntity> posts = petRepository.findByUser_UserId(userId);

        // 각 게시글에 등록된 이미지 파일 삭제
        for (PetEntity post : posts) {
            if (post.getPetImg()!= null && !post.getPetImg().equals("default.png")) {
                File file = new File(path + File.separator + post.getPetImg());
                if (file.exists()) {
                    file.delete();
                }
            }
        }
        petRepository.deleteByUser_UserId(userId);
    }

    @Override
    public List<PetDTO> findPetsByUserId(Long userId) {
        List<PetEntity> petEntities = petRepository.findByUserUserId(userId.longValue());

        List<PetDTO> petDTOList = new ArrayList<>();
        for (PetEntity pet : petEntities) {
            PetDTO dto = new PetDTO();
            dto.setPetId(pet.getPetId());
            dto.setPetName(pet.getPetName());
            dto.setPetBog(pet.getPetBog());
            dto.setPetNeuter(pet.getPetNeuter());
            dto.setPetHbd(pet.getPetHbd());
            dto.setPetImgName(pet.getPetImg());

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
