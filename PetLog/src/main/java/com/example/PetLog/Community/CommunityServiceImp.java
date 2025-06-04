package com.example.PetLog.Community;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CommunityServiceImp implements CommunityService{

    @Autowired
    CommunityRepository communityRepository;

}
