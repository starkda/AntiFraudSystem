package com.example.Antifraud.persistence.repositories;

import com.example.Antifraud.persistence.entities.CardLimitEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public
interface CardLimitRepository extends CrudRepository<CardLimitEntity, String> {
    Optional<CardLimitEntity> getByNumber(String number);
}
