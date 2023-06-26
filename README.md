# AntiFraudSystem

## Overview
This is a system that performs such actions as validating, permititing/declining payment transactions, that are represented as web HTTP requests. System also supports authentication, authorization and data persistence.

## Technologies applied
  - Java
  - Gradle
  - Spring Boot
  - Hibernate
  - H2
  - JPA
  - Jackson
  - Lombok
## Functionality
### Transaction Management
  Each transaction has the following data
  - amount of money sent
  - ip of the sender
  - region of the sender
  - date when money were sent
  
  Transactions are processed automatically on a server, there are 3 possible results: allowed, prohibited, waiting for manual processing. Result depends on several factors:
   - Amount of money. Each card number is associated with 2 numbers: soft limit and hard limit, if transaction exceeds soft limit, transaction waits for manual processing, if transaction exceeds hard limit, it is prohibited.
   - if ip or card number is in blacklist, transaction prohibited
   - if for the last hour we've already got 2 transaction with the same ip, transaction is sent for manual processing
   - if for the last hour we've already got more than 2 with the same ip, transaction is prohibited
   - if for the last hour we've already got 2 transaction with the same card number, transaction is sent for manual processing
   - if for the last hour we've already got more than 2 with the same card number, transaction is prohibited
   
  Order of priorities is following (from least to most) allowed -> manual-processing -> prohibited
  
  Users with role "support" may leave feedback on transaction. So each support may also decide whether transaction must be allowed, sent for manual processing or prohibited. Note, that leaving feedback is not manual processing. Depending on feedback, soft and hard limits for a card number associated with transaction can be changed. They are changed according to following table:
  https://imgur.com/a/rbZoecm
  In case feedback is the same as system result, server returns code 422.
  
  Also, all the data is validated, for example card number checksum check and valid ip number
 
### Authorization
  System supports 4 roles:
  - Unathorized: it can only register/login to the system
  - Merchant: merchants may send transaction to the server
  - Administrator: administrators can change user's role as well as block/unblock users.
  - Support: supports can add/remove card numbers and ips to/from blacklist. They also can give a feedback to the transaction.
  
  Each role has it's own set of accessible routes: https://imgur.com/YgSaYAL
  
 ### Demo:
   <a href="vk.com"> demo </a>
   
## How to build the project
```
$ git clone https://github.com/Antony-Sk/Task-Tracker](https://github.com/starkda/AntiFraudSystem)
$ cd AntiFraudSystem
$ docker-compose up
```
