package com.bank.payment_portal;

import com.bank.payment_portal.entity.Account;
import com.bank.payment_portal.entity.User;
import com.bank.payment_portal.repository.AccountRepository;
import com.bank.payment_portal.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;

    public DataInitializer(UserRepository userRepository, AccountRepository accountRepository) {
        this.userRepository = userRepository;
        this.accountRepository = accountRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        // Agar pehle se data nahi hai tabhi test accounts add honge
        if (userRepository.count() == 0) {
            
            // 1. akash kumar ka Account create karna (₹50,000 Balance)
            User akashUser = new User("Akash Kumar", "password123", "ROLE_CUSTOMER");
            userRepository.save(akashUser);

            Account akashAccount = new Account();
            akashAccount.setAccountNumber("501004289123");
            akashAccount.setBalance(new BigDecimal("70000.00"));
            akashAccount.setStatus("ACTIVE");
            akashAccount.setUser(akashUser);
            accountRepository.save(akashAccount);

            // 2. gurkirpal ka Account create karna (₹67,000 Balance)
            User gurkirpalUser = new User("Gurkirpal Singh", "password123", "ROLE_CUSTOMER");
            userRepository.save(gurkirpalUser);

            Account gurkirpalAccount = new Account();
            gurkirpalAccount.setAccountNumber("50100019283746");
            gurkirpalAccount.setBalance(new BigDecimal("67000.00"));
            gurkirpalAccount.setStatus("ACTIVE");
            gurkirpalAccount.setUser(gurkirpalUser);
            accountRepository.save(gurkirpalAccount);


            // 3. HarshPreet Kaur ka Account create karna (₹60,000 Balance)
            User harshUser = new User("HarshPreet Kaur", "password123", "ROLE_CUSTOMER");
            userRepository.save(harshUser);

            Account harshAccount = new Account();
            harshAccount.setAccountNumber("501008742915");
            harshAccount.setBalance(new BigDecimal("600000.00"));
            harshAccount.setStatus("ACTIVE");
            harshAccount.setUser(harshUser);
            accountRepository.save(harshAccount);



            System.out.println(">>> FinSecure: Test Accounts Initialized Successfully (akash: 501004289123, gurkirpal: 50100019283746) <<<");
        }
    }
}