package com.example.PetLog.Community;

import com.example.PetLog.Comments.CommentsEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommunityServiceImp implements CommunityService{

    @Autowired
    CommunityRepository communityRepository;

    @Override
    public void insertpost(CommunityEntity communityEntity) {
        communityRepository.save(communityEntity);
    }

    @Override
    public List<CommunityEntity> allout() {
        return communityRepository.findAll();
    }

    @Override
    public CommunityEntity findById(Long num) {
        return communityRepository.findById(num).orElse(null);
    }

    @Override
    public void readup(Long num) {
        communityRepository.readup(num);
    }

    @Override
    public CommunityEntity updateById(Long unum) {
        return communityRepository.findById(unum).orElse(null);
    }

    @Override
    public CommunityEntity deleteById(Long dnum) {
        return communityRepository.findById(dnum).orElse(null);
    }

    @Override
    public void deletesave(Long dnum) {
        communityRepository.deleteById(dnum);
    }
}
