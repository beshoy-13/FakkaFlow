package com.fakkaflow.logic.service;

import com.fakkaflow.data.model.*;
import com.fakkaflow.data.repository.*;
import com.fakkaflow.logic.engine.RolloverEngine;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class BudgetService {
    private final CycleRepository cycleRepository = new CycleRepository();
    private final TransactionRepository transactionRepository = new TransactionRepository();
    private final BudgetRepository budgetRepository = new BudgetRepository();
    private final RolloverEngine rolloverEngine = new RolloverEngine();

    public BudgetCycle startCycle(int userId, float totalAllowance, LocalDate startDate, LocalDate endDate) throws Exception {
        if (totalAllowance <= 0) throw new Exception("Budget amount must be greater than zero.");
        if (endDate.isBefore(startDate)) throw new Exception("End date must be after start date.");
        BudgetCycle cycle = new BudgetCycle(userId, totalAllowance, startDate, endDate);
        cycleRepository.save(cycle);
        return cycle;
    }

    public float calculateSafeDailyLimit(int userId) {
        BudgetCycle cycle = cycleRepository.load(userId);
        if (cycle == null) return 0;
        float remaining = calculateRemainingBalance(userId);
        long daysLeft = ChronoUnit.DAYS.between(LocalDate.now(), cycle.getEndDate());
        if (daysLeft <= 0) return 0;
        return remaining / daysLeft;
    }

    public float calculateRemainingBalance(int userId) {
        BudgetCycle cycle = cycleRepository.load(userId);
        if (cycle == null) return 0;
        float totalExpenses = transactionRepository.getTotalByTypeAndUser(userId, "expense");
        return cycle.getTotalAllowance() - totalExpenses;
    }

    public float getTotalIncome(int userId) {
        return transactionRepository.getTotalByTypeAndUser(userId, "income");
    }

    public float getTotalExpenses(int userId) {
        return transactionRepository.getTotalByTypeAndUser(userId, "expense");
    }

    public void recalculateBudget(int userId) {
    }

    public void handleDynamicRollover(int userId) {
        BudgetCycle current = cycleRepository.load(userId);
        if (current == null) return;
        float unspent = rolloverEngine.calculateUnspent(userId, current.getTotalAllowance());
        if (unspent > 0) {
            rolloverEngine.redistributeUnspent(userId, unspent);
        }
    }

    public void saveBudget(Budget budget) throws Exception {
        if (budget.getAmountLimit() <= 0) throw new Exception("Budget limit must be greater than zero.");
        budgetRepository.save(budget);
    }

    public List<Budget> getBudgets(int userId) {
        List<Budget> budgets = budgetRepository.findAll(userId);
        for (Budget b : budgets) {
            float spent = transactionRepository.getSpentByCategory(userId, b.getCategoryId());
            b.setSpent(spent);
        }
        return budgets;
    }

    public BudgetCycle getCurrentCycle(int userId) {
        return cycleRepository.load(userId);
    }

    public void deleteBudget(int budgetId) {
        budgetRepository.delete(budgetId);
    }
}
