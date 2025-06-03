package com.example.PetLog.Diary;

import com.example.PetLog.Pet.PetDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DiaryServiceImp implements DiaryService {

    @Autowired
    DiaryRepository diaryRepository;

    @Override
    public List<PetDTO> petByUser() {
        return null;
    }

    @Override
    public void save(DiaryEntity diaryEntity) {
        diaryRepository.save(diaryEntity);
    }
}
