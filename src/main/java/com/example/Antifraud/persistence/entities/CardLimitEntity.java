package com.example.Antifraud.persistence.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity(name = "cardLimits")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public
class CardLimitEntity {
    @Id
    String number;
    double soft;
    double hard;
}
