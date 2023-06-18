package com.example.Antifraud;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Optional;

@Entity(name = "cardLimits")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
class CardLimitEntity{
    @Id
    String number;
    double soft;
    double hard;
}

@Component
interface CardLimitRepository extends CrudRepository<CardLimitEntity, String> {
    Optional<CardLimitEntity> getByNumber(String number);
}

@Component
public class CardLimitService {
    @Autowired
    CardLimitRepository cardLimitRepository;
    void save(CardLimitEntity cardLimitEntity){
        cardLimitRepository.save(cardLimitEntity);
    }

    CardLimitEntity getCardLimitEntity(String number){
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
