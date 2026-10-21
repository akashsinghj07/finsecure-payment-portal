package com.bank.payment_portal.controller;

import com.bank.payment_portal.dto.TransferRequest;
import com.bank.payment_portal.entity.Account;
import com.bank.payment_portal.entity.Transaction;
import com.bank.payment_portal.repository.AccountRepository;
import com.bank.payment_portal.service.TransferService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class TransferController {

    private final TransferService transferService;
    private final AccountRepository accountRepository;

    public TransferController(TransferService transferService, AccountRepository accountRepository) {
        this.transferService = transferService;
        this.accountRepository = accountRepository;
    }

    // 1. Account Details & Live Balance Fetch karne ke liye
    @GetMapping("/accounts/{accountNumber}")
    public ResponseEntity<?> getAccountDetails(@PathVariable String accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 2. Fund Transfer Execute karne ke liye (POST Endpoint)
    @PostMapping("/transfers")
    public ResponseEntity<?> transferFunds(@RequestBody TransferRequest request) {
        Map<String, Object> response = new HashMap<>();
        try {
            Transaction txn = transferService.executeTransfer(request);
            response.put("status", "SUCCESS");
            response.put("message", "Transfer completed successfully.");
            response.put("transactionId", txn.getId());
            response.put("amount", txn.getAmount());
            response.put("timestamp", txn.getTimestamp());
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException | IllegalStateException e) {
            response.put("status", "FAILED");
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (Exception e) {
            response.put("status", "ERROR");
            response.put("message", "An unexpected server error occurred.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}