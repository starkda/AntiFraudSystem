package com.example.Antifraud.persistence.services;


import com.example.Antifraud.persistence.entities.CardLimitEntity;
import com.example.Antifraud.persistence.repositories.CardLimitRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class CardLimitService {
    @Autowired
    CardLimitRepository cardLimitRepository;
    public void save(CardLimitEntity cardLimitEntity){
        cardLimitRepository.save(cardLimitEntity);
    }

    public CardLimitEntity getCardLimitEntity(String number){
         Optional<CardLimitEntity> record = cardLimitRepository.getByNumber(number);
         if (record.isEmpty()){
             CardLimitEntity ret = new CardLimitEntity(number, 200, 1500);
             cardLimitRepository.save(new CardLimitEntity(number, 200, 1500));
             return ret;
         }
         else{
             return record.get();
         }
    }
}
