package com.example.Antifraud.business;

import com.example.Antifraud.persistence.entities.CardEntity;
import com.example.Antifraud.persistence.entities.IpEntity;
import com.example.Antifraud.persistence.entities.UserEntity;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
public class Response {
    Long id;
    String name;
    String username;
    String role;
    String status;
    String ip;
    String number;
    String info;
    String result;

    public Response(UserEntity kek) {
        this.id = kek.getId();
        this.name = kek.getName();
        this.username = kek.getUsername();
        this.role = kek.getRole();
    }

    public Response(IpEntity ipEntity) {
        this.id = ipEntity.getId();
        this.ip = ipEntity.getIp();
    }

    public Response(CardEntity cardEntity) {
        this.id = cardEntity.getId();
        this.number = cardEntity.getNumber();
    }
}
