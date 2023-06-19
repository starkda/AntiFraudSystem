package com.example.Antifraud.persistence.repositories;

import com.example.Antifraud.persistence.entities.TransactionEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public interface TransactionRepository extends CrudRepository<TransactionEntity, Long> {

    List<TransactionEntity> findAll();

    @Query(value = "SELECT COUNT(*) FROM (SELECT DISTINCT(transactions.ip) FROM transactions WHERE transactions.number=?1 AND transactions.ip!=?2 AND transactions.date BETWEEN ?3 AND ?4)", nativeQuery = true)
    long countCorrelatedIp(String cardNumber, String ip, LocalDateTime l, LocalDateTime r);

    @Query(value = "SELECT COUNT(*) FROM (SELECT DISTINCT(transactions.region) FROM transactions WHERE transactions.number=?1 AND transactions.region!=?2 AND transactions.date BETWEEN ?3 AND ?4)", nativeQuery = true)
    long countCorrelatedRegions(String cardNumber, String region, LocalDateTime l, LocalDateTime r);

    TransactionEntity getTransactionEntityByTransactionId(long id);

    void deleteByTransactionId(long id);

    List<TransactionEntity> getAllByNumber(String number);

}
