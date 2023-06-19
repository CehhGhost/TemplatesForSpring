package com.example.superbank;

import com.example.superbank.model.TransferBalance;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@AllArgsConstructor
public class BankService {

    private final BalanceRepository repository;
    public BigDecimal getBalance(Long accountId) {
        BigDecimal balance =  repository.getBalanceForId(accountId);
        if (balance == null) {
            throw new IllegalArgumentException("no id");
        }
        return balance;
    }

    public BigDecimal addMoney(Long to, BigDecimal amount) {
        final BigDecimal currentBalance = repository.getBalanceForId(to);
        if (currentBalance == null) {
            repository.save(to, amount);
            return amount;
        } else {
            var updatedBalance = currentBalance.add(amount);
            repository.save(to, updatedBalance);
            return updatedBalance;
        }
    }

    public void makeTransfer(TransferBalance transferBalance) {
        final BigDecimal fromBalance = repository.getBalanceForId(transferBalance.getFrom());
        final BigDecimal toBalance = repository.getBalanceForId(transferBalance.getTo());
        if (fromBalance == null || toBalance == null) {
            throw new IllegalArgumentException("no ids");
        }
        if (transferBalance.getAmount().compareTo(fromBalance) > 0) {
            throw new IllegalArgumentException("no money");
        }
        final BigDecimal updatedFromBalance = fromBalance.subtract(transferBalance.getAmount());
        final BigDecimal updatedToBalance = toBalance.add(transferBalance.getAmount());
        repository.save(transferBalance.getFrom(), updatedFromBalance);
        repository.save(transferBalance.getTo(), updatedToBalance);
    }
}
