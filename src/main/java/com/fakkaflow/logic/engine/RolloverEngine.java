package com.fakkaflow.logic.engine;

import com.fakkaflow.data.repository.TransactionRepository;

public class RolloverEngine {
    private final TransactionRepository transactionRepository = new TransactionRepository();

    public float calculateUnspent(int userId, float totalAllowance) {
        float totalSpent = transactionRepository.getTotalByTypeAndUser(userId, "expense");
        float unspent = totalAllowance - totalSpent;
        return Math.max(unspent, 0);
    }

    public float redistributeUnspent(int userId, float unspentAmount) {
        return unspentAmount;
    }
}
