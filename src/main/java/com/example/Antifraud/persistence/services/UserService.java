package com.example.Antifraud.persistence.services;

import com.example.Antifraud.persistence.repositories.UserRepository;
import com.example.Antifraud.persistence.entities.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class UserService {
    @Autowired
    UserRepository kek;
    @Autowired
    PasswordEncoder encoder;

    public void save(UserEntity record) {
        record.setPassword(encoder.encode(record.getPassword()));
        kek.save(record);
    }

    List<UserEntity> findByUsername(String username) {
        return kek.findByUsername(username);
    }

    UserEntity findOneByUsernameAndPassword(String username, String password){
        return kek.findOneByUsernameAndPassword(username, password);
    }

    public UserEntity findOneByUsername(String username){
        List<UserEntity> vals = findByUsername(username);
        if (vals.size() == 0) return null;
        else return vals.get(0);
    }
    public List<UserEntity> getAllRecords() {
        return kek.findAll();
    }

    public void deleteById(Long id) {
        kek.deleteById(id);
    }

    public boolean hasAdmin(){
        return kek.existsByRole("ADMINISTRATOR");
    }

    public void updateRole(UserEntity user, String role){
        kek.deleteById(user.getId());
        user.setRole(role);
        kek.save(user);
    }

    public void updateLock(UserEntity user, String state){
        kek.deleteById(user.getId());
        user.setUnlocked(state.equals("UNLOCK"));
        kek.save(user);
    }
}


