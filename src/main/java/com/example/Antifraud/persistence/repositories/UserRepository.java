package com.example.Antifraud.persistence.repositories;

import com.example.Antifraud.persistence.entities.UserEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public
interface UserRepository extends CrudRepository<UserEntity, Long> {

    UserEntity findOneByUsernameAndPassword(String username, String password);

    boolean existsByRole(String role);

    List<UserEntity> findByUsername(String username);

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    List<UserEntity> findAll();

    void deleteById(Long id);


}
