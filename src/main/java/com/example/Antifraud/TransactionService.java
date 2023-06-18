package com.example.Antifraud;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Entity(name = "transactions")
@Getter
@Setter
class TransactionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long transactionId;
    private long amount;
    private String number;
    private String ip;
    private String region;
    private LocalDateTime date;
    @Column(columnDefinition = "VARCHAR(255) DEFAULT ''", insertable = false)
    private String feedback;
    private String result;

    public TransactionEntity(long amount, String number, String ip, String region, LocalDateTime date, String result) {
        this.amount = amount;
        this.number = number;
        this.ip = ip;
        this.region = region;
        this.date = date;
        this.result = result;
    }

    public TransactionEntity() {

    }

    public TransactionEntity(TransactionRequest data, String result) {
        this.amount = data.getAmount();
        this.number = data.getNumber();
        this.ip = data.getIp();
        this.region = data.getRegion();
        this.date = data.getDate();
        this.result = result;
    }

}


@Component
interface TransactionRepository extends CrudRepository<TransactionEntity, Long> {

    List<TransactionEntity> findAll();

    @Query(value = "SELECT COUNT(*) FROM (SELECT DISTINCT(transactions.ip) FROM transactions WHERE transactions.number=?1 AND transactions.ip!=?2 AND transactions.date BETWEEN ?3 AND ?4)", nativeQuery = true)
    long countCorrelatedIp(String cardNumber, String ip, LocalDateTime l, LocalDateTime r);

    @Query(value = "SELECT COUNT(*) FROM (SELECT DISTINCT(transactions.region) FROM transactions WHERE transactions.number=?1 AND transactions.region!=?2 AND transactions.date BETWEEN ?3 AND ?4)", nativeQuery = true)
    long countCorrelatedRegions(String cardNumber, String region, LocalDateTime l, LocalDateTime r);

    TransactionEntity getTransactionEntityByTransactionId(long id);

    void deleteByTransactionId(long id);

    List<TransactionEntity> getAllByNumber(String number);

}

@Service
public class TransactionService {
    @Autowired
    TransactionRepository transactionRepository;

    void save(TransactionEntity record) {
        transactionRepository.save(record);
    }

    long countCorrelatedIp(String cardNumber, String ip, LocalDateTime transactionDate) {
        return transactionRepository.countCorrelatedIp(cardNumber, ip, transactionDate.minusHours(1), transactionDate);
    }

    long countCorrelatedRegions(String cardNumber, String region, LocalDateTime transactionDate) {
        return transactionRepository.countCorrelatedRegions(cardNumber, region, transactionDate.minusHours(1), transactionDate);
    }

    TransactionEntity getTransaction(long id){
        return transactionRepository.getTransactionEntityByTransactionId(id);
    }

    void updateTransaction(TransactionEntity transaction){
        transactionRepository.save(transaction);
    }

    List<TransactionEntity> getAllTransactionsByCardNumber(String number){
        return transactionRepository.getAllByNumber(number);
    }

    List<TransactionEntity> getAllTransactions(){
        return transactionRepository.findAll();
    }
}


