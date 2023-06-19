package com.example.Antifraud.persistence.services;

import com.example.Antifraud.persistence.entities.IpEntity;
import com.example.Antifraud.persistence.repositories.IpRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class IpService {
    @Autowired
    IpRepository ipRepository;

    public void save(IpEntity record) {
        ipRepository.save(record);
    }

    public List<IpEntity> getAllRecords() {
        return ipRepository.findAll();
    }

    void deleteById(Long id) {
        ipRepository.deleteById(id);
    }

    public void deleteByIp(String ip) {
        IpEntity instance = getByIp(ip);
        ipRepository.deleteById(instance.getId());
    }


    public IpEntity getByIp(String ip){
        return ipRepository.findByIp(ip);
    }

    public boolean containsByIp(String ip){
        return ipRepository.existsByIp(ip);
    }

}


