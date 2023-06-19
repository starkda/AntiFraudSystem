package com.example.Antifraud.business;

import com.example.Antifraud.persistence.entities.*;
import com.example.Antifraud.persistence.services.*;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
class fill {

    @Autowired
    UserService userService;


    @Autowired
    TransactionService transactionService;

    @PostConstruct
    public void run() throws Exception {
        ResponseEntity<Response> kek = null;
        userService.save(new UserEntity("kek", "kek", "1", "ADMINISTRATOR", true));
        userService.save(new UserEntity("kek", "mem", "1", "SUPPORT", true));
        userService.save(new UserEntity("kek", "lol", "1", "MERCHANT", true));

        transactionService.save(new TransactionEntity(1, "1", "1", "1", LocalDateTime.now(), "ALLOWED"));
        transactionService.save(new TransactionEntity(1, "1", "1", "1", LocalDateTime.now().plusDays(1), "ALLOWED"));
        transactionService.save(new TransactionEntity(1, "1", "1", "1", LocalDateTime.now().plusDays(2), "ALLOWED"));
    }


}

@RestController
public class Api {

    @Autowired
    UserService userService;

    @Autowired
    IpService ipService;

    @Autowired
    CardService cardService;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    TransactionService transactionService;

    @Autowired
    CardLimitService cardLimitService;

    void updateBorders(CardLimitEntity cardLimitEntity, long amount, String result, String feedback) {
        double borderAllowed = cardLimitEntity.getSoft();
        double borderProcess = cardLimitEntity.getHard();
        switch (result) {
            case "ALLOWED":
                switch (feedback) {
                    case "MANUAL_PROCESSING":
                        borderAllowed = 0.8 * borderAllowed - 0.2 * amount;
                        break;
                    case "PROHIBITED":
                        borderAllowed = 0.8 * borderAllowed - 0.2 * amount;
                        borderProcess = 0.8 * borderProcess - 0.2 * amount;
                        break;
                    default:
                        break;

                }
                break;
            case "MANUAL_PROCESSING":
                switch (feedback) {
                    case "ALLOWED":
                        borderAllowed = 0.8 * borderAllowed + 0.2 * amount;
                        break;
                    case "PROHIBITED":
                        borderProcess = 0.8 * borderProcess - 0.2 * amount;
                        break;
                    default:
                        break;

                }
                break;
            case "PROHIBITED":
                switch (feedback) {
                    case "ALLOWED":
                        borderAllowed = 0.8 * borderAllowed + 0.2 * amount;
                        borderProcess = 0.8 * borderProcess + 0.2 * amount;
                        break;
                    case "MANUAL_PROCESSING":
                        borderProcess = 0.8 * borderProcess + 0.2 * amount;
                        break;
                    default:
                        break;

                }
                break;
            default:
                break;
        }
        borderAllowed = Math.ceil(borderAllowed);
        borderProcess = Math.ceil(borderProcess);
        cardLimitEntity.setSoft(borderAllowed);
        cardLimitEntity.setHard(borderProcess);
        cardLimitService.save(cardLimitEntity);
    }


    @PostMapping("/api/auth/user")
    ResponseEntity<Response> manageRegistration(@RequestBody UserRegisterRequest userRegisterRequest) {
        if (!userRegisterRequest.valid()) return ResponseEntity.status(400).body(null);

        if (userService.hasAdmin()) {
            UserEntity kek = userService.findOneByUsername(userRegisterRequest.username.toLowerCase());
            if (Objects.isNull(kek)) {
                UserEntity newRecord = new UserEntity(
                        userRegisterRequest.name,
                        userRegisterRequest.username.toLowerCase(),
                        userRegisterRequest.password,
                        "MERCHANT",
                        false);
                userService.save(newRecord);
                return ResponseEntity.status(HttpStatus.CREATED).body(new Response(newRecord));
            } else {
                return ResponseEntity.status(409).body(null);
            }
        } else {
            UserEntity newRecord = new UserEntity(
                    userRegisterRequest.name,
                    userRegisterRequest.username.toLowerCase(),
                    userRegisterRequest.password,
                    "ADMINISTRATOR",
                    true);
            userService.save(newRecord);
            return ResponseEntity.status(HttpStatus.CREATED).body(new Response(newRecord));
        }
    }

    @GetMapping("hui/{cardNumber}")
    CardLimitEntity hui(@PathVariable String cardNumber) {
        return cardLimitService.getCardLimitEntity(cardNumber);
    }

    @GetMapping("/api/auth/list")
    List<Response> UserList() {
        List<UserEntity> keks = userService.getAllRecords();
        List<Response> ret = new ArrayList<>();
        for (UserEntity mem : keks) {
            ret.add(new Response(mem));
        }
        //ret.stream().forEach(x -> x.setId(null));
        return ret;
    }

    @DeleteMapping("/api/auth/user/{username}")
    ResponseEntity<Response> deleteUser(@PathVariable String username) {
        UserEntity kek = userService.findOneByUsername(username.toLowerCase());
        if (Objects.isNull(kek)) {
            return ResponseEntity.notFound().build();
        } else {
            userService.deleteById(kek.getId());
            Response response = new Response();
            response.username = username;
            response.status = "Deleted successfully!";
            return ResponseEntity.ok().body(response);
        }
    }

    @GetMapping("/api/antifraud/history/{cardNumber}")
    ResponseEntity<List<TransactionEntity>> getTransactionsByCardNumber(@PathVariable String cardNumber) {
        CardRequest request = new CardRequest(cardNumber);
        if (!request.valid()) return ResponseEntity.badRequest().build();
        List<TransactionEntity> ret = transactionService.getAllTransactionsByCardNumber(request.number);
        if (ret.size() == 0) return ResponseEntity.notFound().build();
        return ResponseEntity.ok().body(ret);
    }

    @GetMapping("/api/antifraud/history")
    ResponseEntity<List<TransactionEntity>> getAllTransactions() {
        return ResponseEntity.ok().body(transactionService.getAllTransactions());
    }

    @PutMapping("/api/antifraud/transaction")
    ResponseEntity<TransactionEntity> manageTransaction(@RequestBody SupportRequest supportRequest) {
        if (!supportRequest.valid()) return ResponseEntity.badRequest().build();

        TransactionEntity transaction = transactionService.getTransaction(supportRequest.getTransactionId());

        if (Objects.isNull(transaction)) return ResponseEntity.notFound().build();
        if (!transaction.getFeedback().equals("")) return ResponseEntity.status(409).build();

        transaction.setFeedback(supportRequest.getFeedback());


        if (transaction.getFeedback().equals(transaction.getResult())) {
            return ResponseEntity.unprocessableEntity().build();
        } else {
            transactionService.updateTransaction(transaction);
            CardLimitEntity record = cardLimitService.getCardLimitEntity(transaction.getNumber());
            System.out.println("Before update: " + record.getSoft() + " " + record.getHard());
            updateBorders(record, transaction.getAmount(), transaction.getResult(), transaction.getFeedback());
            System.out.println("After update: " + record.getSoft() + " " + record.getHard());
            return ResponseEntity.ok().body(transaction);
        }
    }

    @PostMapping("/api/antifraud/transaction")
    ResponseEntity<Response> rResponse(@RequestBody TransactionRequest request) {

        if (!request.validIp() || !request.validNumber() || !request.validRegion())
            return ResponseEntity.badRequest().build();

        CardLimitEntity cardLimitEntity = cardLimitService.getCardLimitEntity(request.getNumber());
        double borderAllowed = cardLimitEntity.getSoft();
        double borderProcess = cardLimitEntity.getHard();
        System.out.println("Borders for transaction are: " + borderAllowed + " " + borderProcess);
        String result = "";
        StringBuilder info = new StringBuilder("none, ");

        if (request.amount <= 0) {
            return ResponseEntity.badRequest().body(null);
        }
        if (request.amount <= borderAllowed) result = "ALLOWED";
        else if (request.amount <= borderProcess) {
            result = "MANUAL_PROCESSING";
            info = new StringBuilder("amount, ");
        } else {
            result = "PROHIBITED";
            info = new StringBuilder("amount, ");
        }

        if (cardService.containsByNumber(request.getNumber())) {
            if (result.equals("PROHIBITED")) {
                info.append("card-number, ");
            } else {
                result = "PROHIBITED";
                info = new StringBuilder("card-number, ");
            }
        }

        if (ipService.containsByIp(request.getIp())) {
            if (result.equals("PROHIBITED")) {
                info.append("ip, ");
            } else {
                result = "PROHIBITED";
                info = new StringBuilder("ip, ");
            }
        }

        if (transactionService.countCorrelatedIp(request.number, request.ip, request.date) == 2) {
            if (result.equals("ALLOWED")) {
                result = "MANUAL_PROCESSING";
                info = new StringBuilder("ip-correlation, ");
            } else if (result.equals("MANUAL_PROCESSING")) {
                info.append("ip-correlation, ");
            }
        }

        if (transactionService.countCorrelatedIp(request.number, request.ip, request.date) > 2) {
            if (result.equals("ALLOWED") || result.equals("MANUAL_PROCESSING")) {
                result = "PROHIBITED";
                info = new StringBuilder("ip-correlation, ");
            } else {
                info.append("ip-correlation, ");
            }
        }

        if (transactionService.countCorrelatedRegions(request.number, request.region, request.date) == 2) {
            if (result.equals("ALLOWED")) {
                result = "MANUAL_PROCESSING";
                info = new StringBuilder("region-correlation, ");
            } else if (result.equals("MANUAL_PROCESSING")) {
                info.append("region-correlation, ");
            }
        }

        if (transactionService.countCorrelatedRegions(request.number, request.region, request.date) > 2) {
            if (result.equals("ALLOWED") || result.equals("MANUAL_PROCESSING")) {
                result = "PROHIBITED";
                info = new StringBuilder("region-correlation, ");
            } else {
                info.append("region-correlation, ");
            }
        }

        transactionService.save(new TransactionEntity(request, result));
        Response response = new Response();
        response.result = result;
        response.info = info.substring(0, info.length() - 2);
        return ResponseEntity.ok().body(response);

    }

    @PutMapping("/api/auth/role")
    ResponseEntity<Response> roleResponse(@RequestBody RoleChangeRequest roleChangeRequest) {
        if (!roleChangeRequest.valid()) return ResponseEntity.badRequest().build();
        UserEntity kek = userService.findOneByUsername(roleChangeRequest.username.toLowerCase());
        if (Objects.isNull(kek)) {
            return ResponseEntity.notFound().build();
        }
        if (kek.getRole().equals(roleChangeRequest.role)) {
            return ResponseEntity.status(409).build();
        }
        userService.updateRole(kek, roleChangeRequest.getRole());
        return ResponseEntity.ok().body(new Response(kek));
    }

    @PutMapping("/api/auth/access")
    ResponseEntity<Response> lockResponse(@RequestBody LockChangeRequest lockChangeRequest) {
        if (!lockChangeRequest.valid()) return ResponseEntity.badRequest().build();

        UserEntity kek = userService.findOneByUsername(lockChangeRequest.username.toLowerCase());
        if (Objects.isNull(kek)) {
            return ResponseEntity.notFound().build();
        }
        if (kek.getRole().equals("ADMINISTRATOR")) return ResponseEntity.badRequest().build();

        userService.updateLock(kek, lockChangeRequest.getOperation());
        Response ret = new Response();
        ret.setStatus("User " + kek.getUsername() + " " + (lockChangeRequest.operation.equals("LOCK") ? "locked!" : "unlocked!"));
        return ResponseEntity.ok().body(ret);
    }


    @PostMapping("/api/antifraud/suspicious-ip")
    ResponseEntity<Response> addIp(@RequestBody SuspiciousIpRequest suspiciousIpRequest) {
        if (!suspiciousIpRequest.valid()) return ResponseEntity.badRequest().build();
        IpEntity instance = ipService.getByIp(suspiciousIpRequest.ip);
        if (Objects.isNull(instance)) {
            instance = new IpEntity(suspiciousIpRequest.ip);
            ipService.save(instance);
            return ResponseEntity.ok().body(new Response(instance));
        } else {
            return ResponseEntity.status(409).build();
        }
    }

    @DeleteMapping("/api/antifraud/suspicious-ip/{ip}")
    ResponseEntity<Response> deleteIp(@PathVariable String ip) {
        SuspiciousIpRequest suspiciousIpRequest = new SuspiciousIpRequest(ip);
        if (!suspiciousIpRequest.valid()) return ResponseEntity.badRequest().build();
        IpEntity instance = ipService.getByIp(suspiciousIpRequest.ip);
        if (Objects.isNull(instance)) {
            return ResponseEntity.notFound().build();
        } else {
            ipService.deleteByIp(ip);
            Response response = new Response();
            response.status = String.format("IP %s successfully removed!", ip);
            return ResponseEntity.ok().body(response);
        }
    }

    @GetMapping("/api/antifraud/suspicious-ip")
    ResponseEntity<List<Response>> getIp() {
        List<Response> ret = new ArrayList<>();
        for (IpEntity ip : ipService.getAllRecords()) {
            ret.add(new Response(ip));
        }
        return ResponseEntity.ok().body(ret);
    }


    @PostMapping("/api/antifraud/stolencard")
    ResponseEntity<Response> addCard(@RequestBody CardRequest cardRequest) {
        if (!cardRequest.valid()) return ResponseEntity.badRequest().build();
        CardEntity instance = cardService.getByNumber(cardRequest.number);
        if (Objects.isNull(instance)) {
            instance = new CardEntity(cardRequest.number);
            cardService.save(instance);
            return ResponseEntity.ok().body(new Response(instance));
        } else {
            return ResponseEntity.status(409).build();
        }
    }

    @DeleteMapping("/api/antifraud/stolencard/{number}")
    ResponseEntity<Response> deleteNumber(@PathVariable String number) {
        CardRequest cardRequest = new CardRequest(number);
        if (!cardRequest.valid()) return ResponseEntity.badRequest().build();
        CardEntity instance = cardService.getByNumber(cardRequest.number);
        if (Objects.isNull(instance)) {
            return ResponseEntity.notFound().build();
        } else {
            cardService.deleteByNumber(number);
            Response response = new Response();
            response.status = String.format("Card %s successfully removed!", number);
            return ResponseEntity.ok().body(response);
        }
    }

    @GetMapping("/api/antifraud/stolencard")
    ResponseEntity<List<Response>> getNumber() {
        List<Response> ret = new ArrayList<>();
        for (CardEntity card : cardService.getAllRecords()) {
            ret.add(new Response(card));
        }
        return ResponseEntity.ok().body(ret);
    }

}


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
class SupportRequest implements Validatable{
    long transactionId;
    String feedback;

    @Override
    public boolean valid() {
        return (feedback.equals("ALLOWED") || feedback.equals("MANUAL_PROCESSING") || feedback.equals("PROHIBITED"));
    }
}

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
class CardRequest implements Validatable{
    String number;

    @Override
    public boolean valid() throws NumberFormatException {
        //Luhn's algorithm to check card's validity
        if (number == null) return false;
        if (number.length() != 16) return false;
        try {
            long val = Long.parseLong(number);
            System.out.println(val);
            int sum = 0;
            for (int i = 16; i > 0; i--) {
                int value = Integer.parseInt(String.valueOf(number.charAt(i - 1)));
                if (i % 2 == 0) {
                    sum += value;
                } else {
                    if (2 * value > 9) sum += 2 * value - 9;
                    else sum += 2 * value;
                }
            }
            System.out.println(sum);
            return (sum % 10 == 0);
        } catch (NumberFormatException e) {
            return false;
        }
    }
}


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
class SuspiciousIpRequest implements Validatable{
    String ip;

    @Override
    public boolean valid() throws NumberFormatException {
        if (ip == null) return false;
        List<String> vals = List.of(ip.split("\\."));
        if (vals.size() != 4) return false;
        try {
            for (int i = 0; i < 4; i++) {
                int currentNumber = Integer.parseInt(vals.get(i));
                if (currentNumber < 0 || currentNumber > 255) return false;
            }
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}


@Getter
@Setter
class LockChangeRequest implements Validatable {
    String username;
    String operation;

    @Override
    public boolean valid(){
        if (username == null || operation == null) return false;
        return operation.equals("LOCK") || operation.equals("UNLOCK");
    }
}

@Getter
@Setter
class RoleChangeRequest implements Validatable{
    String username;
    String role;

    @Override
    public boolean valid() {
        if (username == null || role == null) return false;
        return role.equals("SUPPORT") || role.equals("MERCHANT");
    }
}


@Getter
@Setter
class UserRegisterRequest implements Validatable {
    String name;
    String username;
    String password;

    @Override
    public boolean valid() {
        return name != null && username != null && password != null;
    }

}
