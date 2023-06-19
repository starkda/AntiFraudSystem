package com.example.Antifraud.persistence.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity(name = "users")
@Getter
@Setter
public
class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;
    private String username;
    private String password;
    private String role;
    private boolean unlocked;

    public UserEntity(String name, String username, String password, String role, boolean unlocked) {
        this.name = name;
        this.username = username;
        this.password = password;
        this.role = role;
        this.unlocked = unlocked;
    }

    public UserEntity() {

    }

}
