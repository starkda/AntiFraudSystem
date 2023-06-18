package com.example.Antifraud;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.persistence.*;
import java.util.List;

@Entity(name = "ips")
@Getter
@Setter
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


@Component
interface IpRepository extends CrudRepository<IpEntity, Long> {
    List<IpEntity> findById(String username);

    List<IpEntity> findAll();

    IpEntity findByIp(String ip);

    void deleteById(Long id);

    boolean existsByIp(String ip);
}

@Service
public class IpService {
    @Autowired
    IpRepository ipRepository;

    void save(IpEntity record) {
        ipRepository.save(record);
    }

    List<IpEntity> getAllRecords() {
        return ipRepository.findAll();
    }

    void deleteById(Long id) {
        ipRepository.deleteById(id);
    }

    void deleteByIp(String ip) {
        IpEntity instance = getByIp(ip);
        ipRepository.deleteById(instance.getId());
    }


    IpEntity getByIp(String ip){
        return ipRepository.findByIp(ip);
    }

    boolean containsByIp(String ip){
        return ipRepository.existsByIp(ip);
    }

}


