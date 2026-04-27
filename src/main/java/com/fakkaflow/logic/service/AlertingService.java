package com.fakkaflow.logic.service;

import com.fakkaflow.data.model.Budget;
import java.util.ArrayList;
import java.util.List;

public class AlertingService {
    public enum AlertLevel { NONE, WARNING, EXCEEDED }

    public record BudgetAlert(String category, double percent, AlertLevel level, float limit, float spent) {}

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

    public void check80PercentThreshold(Budget budget) {
        double pct = budget.getUsagePercent();
        if (pct >= 80 && pct < 100) sendBudgetWarning(budget);
        else if (pct >= 100) sendBudgetExceededAlert(budget);
    }

    public void sendBudgetWarning(Budget budget) {
        System.out.println("[ALERT] Budget warning: " + budget.getCategoryName() +
            " is at " + String.format("%.1f", budget.getUsagePercent()) + "%");
    }

    public void sendBudgetExceededAlert(Budget budget) {
        System.out.println("[ALERT] Budget exceeded: " + budget.getCategoryName() +
            " exceeded by " + String.format("%.2f", budget.getSpent() - budget.getAmountLimit()));
    }
}
