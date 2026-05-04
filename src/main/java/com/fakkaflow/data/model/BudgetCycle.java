package com.fakkaflow.data.model;

import java.time.LocalDate;
/**
 * Represents a budget cycle for a user.
 *
 * Defines the total allowance and the time range for budgeting.
 */
public class BudgetCycle {
    private int cycleId;
    private int userId;
    private float totalAllowance;
    private LocalDate startDate;
    private LocalDate endDate;
    /**
     * Default constructor.
     */
    public BudgetCycle() {}

    /**
     * Creates a new budget cycle.
     *
     * @param userId ID of the user
     * @param totalAllowance total budget for the cycle
     * @param startDate start date of the cycle
     * @param endDate end date of the cycle
     */
    public BudgetCycle(int userId, float totalAllowance, LocalDate startDate, LocalDate endDate) {
        this.userId = userId;
        this.totalAllowance = totalAllowance;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public int getCycleId() { return cycleId; }
    public void setCycleId(int cycleId) { this.cycleId = cycleId; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public float getTotalAllowance() { return totalAllowance; }
    public void setTotalAllowance(float totalAllowance) { this.totalAllowance = totalAllowance; }
    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
}
