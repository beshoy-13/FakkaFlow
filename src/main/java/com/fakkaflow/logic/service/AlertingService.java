package com.fakkaflow.logic.service;

import com.fakkaflow.data.model.Budget;
import java.util.ArrayList;
import java.util.List;
/**
 * Service responsible for monitoring budgets and generating alerts.
 * Provides warnings when spending reaches certain thresholds.
 */
public class AlertingService {
    /**
     * Alert levels for budget usage.
     */
    public enum AlertLevel { NONE, WARNING, EXCEEDED }
    /**
     * Record representing a budget alert.
     *
     * @param category category name
     * @param percent usage percentage
     * @param level alert level
     * @param limit budget limit
     * @param spent amount spent
     */
    public record BudgetAlert(String category, double percent, AlertLevel level, float limit, float spent) {}
    /**
     * Checks all budgets and generates alerts.
     *
     * @param budgets list of budgets
     * @return list of alerts
     */
    public List<BudgetAlert> checkBudgets(List<Budget> budgets) {
        List<BudgetAlert> alerts = new ArrayList<>();
        for (Budget b : budgets) {
            double pct = b.getUsagePercent();
            if (pct >= 100) {
                alerts.add(new BudgetAlert(b.getCategoryName(), pct, AlertLevel.EXCEEDED, b.getAmountLimit(), b.getSpent()));
            } else if (pct >= 80) {
                alerts.add(new BudgetAlert(b.getCategoryName(), pct, AlertLevel.WARNING, b.getAmountLimit(), b.getSpent()));
            }
        }
        return alerts;
    }
    /**
     * Checks if a budget crosses warning thresholds.
     *
     * @param budget budget object
     */
    public void check80PercentThreshold(Budget budget) {
        double pct = budget.getUsagePercent();
        if (pct >= 80 && pct < 100) sendBudgetWarning(budget);
        else if (pct >= 100) sendBudgetExceededAlert(budget);
    }
    /**
     * Sends a warning alert when usage exceeds 80%.
     *
     * @param budget budget object
     */
    public void sendBudgetWarning(Budget budget) {
        System.out.println("[ALERT] Budget warning: " + budget.getCategoryName() +
            " is at " + String.format("%.1f", budget.getUsagePercent()) + "%");
    }
    /**
     * Sends an alert when budget is exceeded.
     *
     * @param budget budget object
     */
    public void sendBudgetExceededAlert(Budget budget) {
        System.out.println("[ALERT] Budget exceeded: " + budget.getCategoryName() +
            " exceeded by " + String.format("%.2f", budget.getSpent() - budget.getAmountLimit()));
    }
}
