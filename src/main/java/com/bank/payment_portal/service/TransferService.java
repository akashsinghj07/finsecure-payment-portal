package com.bank.payment_portal.service;

import com.bank.payment_portal.dto.TransferRequest;
import com.bank.payment_portal.entity.Account;
import com.bank.payment_portal.entity.Transaction;
import com.bank.payment_portal.repository.AccountRepository;
import com.bank.payment_portal.repository.TransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class TransferService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    public TransferService(AccountRepository accountRepository, TransactionRepository transactionRepository) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }

    @Transactional(rollbackFor = Exception.class)
    public Transaction executeTransfer(TransferRequest request) {
        // 1. Validation Checks
        if (request.getAmount() == null || request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Transfer amount must be strictly greater than zero.");
        }

        if (request.getSourceAccountNumber().equalsIgnoreCase(request.getDestinationAccountNumber())) {
            throw new IllegalArgumentException("Self-transfers are not allowed.");
        }

        // 2. Fetch Accounts
        Account source = accountRepository.findByAccountNumber(request.getSourceAccountNumber())
                .orElseThrow(() -> new IllegalArgumentException("Source account not found: " + request.getSourceAccountNumber()));

        Account destination = accountRepository.findByAccountNumber(request.getDestinationAccountNumber())
                .orElseThrow(() -> new IllegalArgumentException("Destination account not found: " + request.getDestinationAccountNumber()));

        // 3. Status Check
        if (!"ACTIVE".equalsIgnoreCase(source.getStatus()) || !"ACTIVE".equalsIgnoreCase(destination.getStatus())) {
            throw new IllegalStateException("One or both accounts are not active.");
        }

        // 4. Balance Invariant (Overdraft Prevention)
        if (source.getBalance().compareTo(request.getAmount()) < 0) {
            throw new IllegalArgumentException("Insufficient balance. Available: ₹" + source.getBalance());
        }

        // 5. Debit & Credit
        source.setBalance(source.getBalance().subtract(request.getAmount()));
        destination.setBalance(destination.getBalance().add(request.getAmount()));

        accountRepository.save(source);
        accountRepository.save(destination);

        // 6. Record Transaction Entry
        Transaction txn = new Transaction();
        txn.setSourceAccountNumber(source.getAccountNumber());
        txn.setDestinationAccountNumber(destination.getAccountNumber());
        txn.setAmount(request.getAmount());
        txn.setTimestamp(LocalDateTime.now());
        txn.setStatus("SUCCESS");

        return transactionRepository.save(txn);
    }
}