package com.fakkaflow.data.model;
/**
 * Represents a budget for a specific category for a user.
 *
 * This model is used to track spending limits and usage within a given period.
 */
public class Budget {
    private int id;
    private int userId;
    private int categoryId;
    private String categoryName;
    private float amountLimit;
    private String period;
    private float spent;

    /**
     * Default constructor.
     */
    public Budget() {}
    /**
     * Creates a new budget with basic details.
     *
     * @param userId ID of the user
     * @param categoryId ID of the category
     * @param amountLimit maximum allowed amount
     * @param period budget period (e.g., monthly)
     */
    public Budget(int userId, int categoryId, float amountLimit, String period) {
        this.userId = userId;
        this.categoryId = categoryId;
        this.amountLimit = amountLimit;
        this.period = period;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public int getCategoryId() { return categoryId; }
    public void setCategoryId(int categoryId) { this.categoryId = categoryId; }
    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }
    public float getAmountLimit() { return amountLimit; }
    public void setAmountLimit(float amountLimit) { this.amountLimit = amountLimit; }
    public String getPeriod() { return period; }
    public void setPeriod(String period) { this.period = period; }
    public float getSpent() { return spent; }
    public void setSpent(float spent) { this.spent = spent; }

    /**
     * Calculates the percentage of the budget that has been used.
     *
     * @return usage percentage (0–100)
     */
    public double getUsagePercent() {
        if (amountLimit <= 0) return 0;
        return (spent / amountLimit) * 100.0;
    }
}
