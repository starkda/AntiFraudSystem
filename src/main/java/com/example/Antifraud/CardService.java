package com.example.Antifraud;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.persistence.*;
import java.util.List;

@Entity(name = "cards")
@Getter
@Setter
class CardEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String number;

    public CardEntity(String number) {
        this.number = number;
    }

    public CardEntity() {

    }

}


@Component
interface CardRepository extends CrudRepository<CardEntity, Long> {
    List<CardEntity> findById(String username);

    List<CardEntity> findAll();

    CardEntity findByNumber(String number);

    void deleteById(Long id);

    boolean existsByNumber(String number);
}

@Service
public class CardService {
    @Autowired
    CardRepository cardRepository;

    void save(CardEntity record) {
        cardRepository.save(record);
    }

    List<CardEntity> getAllRecords() {
        return cardRepository.findAll();
    }

    void deleteById(Long id) {
        cardRepository.deleteById(id);
    }

    void deleteByNumber(String number) {
        CardEntity instance = getByNumber(number);
        deleteById(instance.getId());
    }


    CardEntity getByNumber(String number) {
        return cardRepository.findByNumber(number);
    }

    boolean containsByNumber(String number){
        return cardRepository.existsByNumber(number);
    }

}


