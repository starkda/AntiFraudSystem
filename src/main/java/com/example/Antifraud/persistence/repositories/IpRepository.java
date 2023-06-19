package com.example.Antifraud.persistence.repositories;

import com.example.Antifraud.persistence.entities.IpEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public
interface IpRepository extends CrudRepository<IpEntity, Long> {
    List<IpEntity> findById(String username);

    List<IpEntity> findAll();

    IpEntity findByIp(String ip);

    void deleteById(Long id);

    boolean existsByIp(String ip);
}
