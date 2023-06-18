package com.example.Antifraud;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.*;
import javax.transaction.Transaction;
import javax.transaction.TransactionManager;
import java.util.List;

@Entity(name = "users")
@Getter
@Setter
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


@Component
interface UserRepository extends CrudRepository<UserEntity, Long> {

    UserEntity findOneByUsernameAndPassword(String username, String password);

    boolean existsByRole(String role);
    List<UserEntity> findByUsername(String username);

    @Transactional(isolation=Isolation.REPEATABLE_READ)
    List<UserEntity> findAll();

    void deleteById(Long id);


}

@Service
public class UserService {
    @Autowired
    UserRepository kek;
    @Autowired
    PasswordEncoder encoder;

    void save(UserEntity record) {
        record.setPassword(encoder.encode(record.getPassword()));
        kek.save(record);
    }

    List<UserEntity> findByUsername(String username) {
        return kek.findByUsername(username);
    }

    UserEntity findOneByUsernameAndPassword(String username, String password){
        return kek.findOneByUsernameAndPassword(username, password);
    }

    UserEntity findOneByUsername(String username){
        List<UserEntity> vals = findByUsername(username);
        if (vals.size() == 0) return null;
        else return vals.get(0);
    }
    List<UserEntity> getAllRecords() {
        return kek.findAll();
    }

    void deleteById(Long id) {
        kek.deleteById(id);
    }

    boolean hasAdmin(){
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


