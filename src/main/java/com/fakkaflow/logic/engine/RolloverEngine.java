package com.fakkaflow.logic.engine;

import com.fakkaflow.data.repository.TransactionRepository;
/**
 * Engine responsible for handling budget rollover logic.
 * Calculates unspent budget and redistributes it if needed.
 */
public class RolloverEngine {
    private final TransactionRepository transactionRepository = new TransactionRepository();
    /**
     * Calculates the unspent amount from a user's total allowance.
     *
     * @param userId user ID
     * @param totalAllowance total budget allowance
     * @return remaining (unspent) amount, never negative
     */
    public float calculateUnspent(int userId, float totalAllowance) {
        float totalSpent = transactionRepository.getTotalByTypeAndUser(userId, "expense");
        float unspent = totalAllowance - totalSpent;
        return Math.max(unspent, 0);
    }
    /**
     * Redistributes unspent budget.
     * Currently returns the same value (placeholder for future logic).
     *
     * @param userId user ID
     * @param unspentAmount amount to redistribute
     * @return redistributed amount
     */
    public float redistributeUnspent(int userId, float unspentAmount) {
        return unspentAmount;
    }
}
