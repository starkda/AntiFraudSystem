package com.example.Antifraud.business;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public
class TransactionRequest {
    int amount;
    String ip;
    String number;
    String region;
    LocalDateTime date;

    boolean validRegion() {
        switch (region) {
            case "EAP", "ECA", "HIC", "LAC", "MENA", "SA", "SSA":
                return true;
            default:
                return false;
        }
    }

    boolean validNumber() throws NumberFormatException {
        if (number == null) return false;
        if (number.length() != 16) return false;
        try {
            long val = Long.parseLong(number);
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

    boolean validIp() throws NumberFormatException {
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
