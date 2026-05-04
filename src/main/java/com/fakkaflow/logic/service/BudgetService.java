package com.fakkaflow.logic.service;

import com.fakkaflow.data.model.*;
import com.fakkaflow.data.repository.*;
import com.fakkaflow.logic.engine.RolloverEngine;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
/**
 * Service responsible for managing budgets, budget cycles,
 * and financial calculations such as expenses and limits.
 */
public class BudgetService {
    private final CycleRepository cycleRepository = new CycleRepository();
    private final TransactionRepository transactionRepository = new TransactionRepository();
    private final BudgetRepository budgetRepository = new BudgetRepository();
    private final RolloverEngine rolloverEngine = new RolloverEngine();
    /**
     * Starts a new budget cycle.
     *
     * @param userId user ID
     * @param totalAllowance total budget amount
     * @param startDate cycle start date
     * @param endDate cycle end date
     * @return created BudgetCycle
     * @throws Exception if validation fails
     */
    public BudgetCycle startCycle(int userId, float totalAllowance, LocalDate startDate, LocalDate endDate) throws Exception {
        if (totalAllowance <= 0) throw new Exception("Budget amount must be greater than zero.");
        if (endDate.isBefore(startDate)) throw new Exception("End date must be after start date.");
        BudgetCycle cycle = new BudgetCycle(userId, totalAllowance, startDate, endDate);
        cycleRepository.save(cycle);
        return cycle;
    }
    /**
     * Calculates safe daily spending limit based on remaining balance and days left.
     *
     * @param userId user ID
     * @return daily safe limit or 0 if no cycle
     */

    public float calculateSafeDailyLimit(int userId) {
        BudgetCycle cycle = cycleRepository.load(userId);
        if (cycle == null) return 0;
        float remaining = calculateRemainingBalance(userId);
        long daysLeft = ChronoUnit.DAYS.between(LocalDate.now(), cycle.getEndDate());
        if (daysLeft <= 0) return 0;
        return remaining / daysLeft;
    }
    /**
     * Calculates remaining balance for current cycle.
     *
     * @param userId user ID
     * @return remaining balance
     */

    public float calculateRemainingBalance(int userId) {
        BudgetCycle cycle = cycleRepository.load(userId);
        if (cycle == null) return 0;
        float totalExpenses = transactionRepository.getTotalByTypeAndUser(userId, "expense");
        return cycle.getTotalAllowance() - totalExpenses;
    }
    /**
     * Gets total income for user.
     *
     * @param userId user ID
     * @return total income
     */

    public float getTotalIncome(int userId) {
        return transactionRepository.getTotalByTypeAndUser(userId, "income");
    }
    /**
     * Gets total expenses for user.
     *
     * @param userId user ID
     * @return total expenses
     */

    public float getTotalExpenses(int userId) {
        return transactionRepository.getTotalByTypeAndUser(userId, "expense");
    }
    /**
     * Recalculates budget (placeholder for future logic).
     *
     * @param userId user ID
     */
    public void recalculateBudget(int userId) {        // Future enhancement

    }
    /**
     * Handles dynamic rollover of unspent budget.
     *
     * @param userId user ID
     */
    public void handleDynamicRollover(int userId) {
        BudgetCycle current = cycleRepository.load(userId);
        if (current == null) return;
        float unspent = rolloverEngine.calculateUnspent(userId, current.getTotalAllowance());
        if (unspent > 0) {
            rolloverEngine.redistributeUnspent(userId, unspent);
        }
    }

    /**
     * Saves a budget after validation.
     *
     * @param budget budget object
     * @throws Exception if invalid
     */
    public void saveBudget(Budget budget) throws Exception {
        if (budget.getAmountLimit() <= 0) throw new Exception("Budget limit must be greater than zero.");
        budgetRepository.save(budget);
    }
    /**
     * Retrieves all budgets for a user and calculates spent amount.
     *
     * @param userId user ID
     * @return list of budgets
     */
    public List<Budget> getBudgets(int userId) {
        List<Budget> budgets = budgetRepository.findAll(userId);
        for (Budget b : budgets) {
            float spent = transactionRepository.getSpentByCategory(userId, b.getCategoryId());
            b.setSpent(spent);
        }
        return budgets;
    }
    /**
     * Retrieves the current active budget cycle.
     *
     * @param userId user ID
     * @return BudgetCycle or null
     */
    public BudgetCycle getCurrentCycle(int userId) {
        return cycleRepository.load(userId);
    }

    /**
     * Deletes a budget.
     *
     * @param budgetId budget ID
     */
    public void deleteBudget(int budgetId) {
        budgetRepository.delete(budgetId);
    }
}
