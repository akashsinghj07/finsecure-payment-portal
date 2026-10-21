package com.bank.payment_portal.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "accounts")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String accountNumber;

    @Column(nullable = false)
    private BigDecimal balance;

    @Column(nullable = false)
    private String status; // "ACTIVE" or "FROZEN"

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    public Account() {}

    public Account(String accountNumber, BigDecimal balance, String status, User user) {
        this.accountNumber = accountNumber;
        this.balance = balance;
        this.status = status;
        this.user = user;
    }

    public Long getId() { return id; }
    public String getAccountNumber() { return accountNumber; }
    public void setAccountNumber(String accountNumber) { this.accountNumber = accountNumber; }
    public BigDecimal getBalance() { return balance; }
    public void setBalance(BigDecimal balance) { this.balance = balance; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
}