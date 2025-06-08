package com.example.PetLog.Community;

import com.example.PetLog.Comments.CommentsEntity;

import java.util.List;

public interface CommunityService {

    void insertpost(CommunityEntity communityEntity);

    List<CommunityEntity> allout();

    CommunityEntity findById(Long num);

    void readup(Long num);

    CommunityEntity updateById(Long unum);

    CommunityEntity deleteById(Long dnum);

    void deletesave(Long dnum);
}
