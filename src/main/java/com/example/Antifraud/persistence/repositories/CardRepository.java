package com.example.Antifraud.persistence.repositories;

import com.example.Antifraud.persistence.entities.CardEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public
interface CardRepository extends CrudRepository<CardEntity, Long> {
    List<CardEntity> findById(String username);

    List<CardEntity> findAll();

    CardEntity findByNumber(String number);

    void deleteById(Long id);

    boolean existsByNumber(String number);
}
