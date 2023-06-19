package com.example.Antifraud.persistence.entities;

import com.example.Antifraud.business.TransactionRequest;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Entity(name = "transactions")
@Getter
@Setter
public class TransactionEntity {
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