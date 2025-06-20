package com.example.PetLog.QnA;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class QnAServiceImp implements QnAService{

    @Autowired
    QnARepository qnARepository;


}
