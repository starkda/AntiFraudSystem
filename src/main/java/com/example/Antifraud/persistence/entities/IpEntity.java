package com.example.Antifraud.persistence.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity(name = "ips")
@Getter
@Setter
public
class IpEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String ip;

    public IpEntity(String ip) {
        this.ip = ip;
    }

    public IpEntity() {

    }

}
