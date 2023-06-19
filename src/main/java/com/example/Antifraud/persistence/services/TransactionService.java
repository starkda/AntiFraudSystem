package com.example.Antifraud.persistence.services;

import com.example.Antifraud.persistence.entities.TransactionEntity;
import com.example.Antifraud.persistence.repositories.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TransactionService {
    @Autowired
    TransactionRepository transactionRepository;

    public void save(TransactionEntity record) {
        transactionRepository.save(record);
    }

    public long countCorrelatedIp(String cardNumber, String ip, LocalDateTime transactionDate) {
        return transactionRepository.countCorrelatedIp(cardNumber, ip, transactionDate.minusHours(1), transactionDate);
    }

    public long countCorrelatedRegions(String cardNumber, String region, LocalDateTime transactionDate) {
        return transactionRepository.countCorrelatedRegions(cardNumber, region, transactionDate.minusHours(1), transactionDate);
    }

    public TransactionEntity getTransaction(long id){
        return transactionRepository.getTransactionEntityByTransactionId(id);
    }

    public void updateTransaction(TransactionEntity transaction){
        transactionRepository.save(transaction);
    }

    public List<TransactionEntity> getAllTransactionsByCardNumber(String number){
        return transactionRepository.getAllByNumber(number);
    }

    public List<TransactionEntity> getAllTransactions(){
        return transactionRepository.findAll();
    }
}


