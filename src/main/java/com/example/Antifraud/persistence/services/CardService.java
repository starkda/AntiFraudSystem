package com.example.Antifraud.persistence.services;

import com.example.Antifraud.persistence.entities.CardEntity;
import com.example.Antifraud.persistence.repositories.CardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class CardService {
    @Autowired
    CardRepository cardRepository;

    public void save(CardEntity record) {
        cardRepository.save(record);
    }

    public List<CardEntity> getAllRecords() {
        return cardRepository.findAll();
    }

    void deleteById(Long id) {
        cardRepository.deleteById(id);
    }

    public void deleteByNumber(String number) {
        CardEntity instance = getByNumber(number);
        deleteById(instance.getId());
    }


    public CardEntity getByNumber(String number) {
        return cardRepository.findByNumber(number);
    }

    public boolean containsByNumber(String number){
        return cardRepository.existsByNumber(number);
    }

}


