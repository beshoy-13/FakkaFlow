package com.fakkaflow.data.model;
/**
 * Represents a financial goal for a user.
 *
 * Tracks saving progress toward a target amount within a deadline.
 */
public class Goal {
    private int id;
    private int userId;
    private String name;
    private float targetAmount;
    private float savedAmount;
    private String deadline;
    private String createdAt;
    /**
     * Default constructor.
     */
    public Goal() {}

    /**
     * Creates a new financial goal.
     *
     * @param userId ID of the user
     * @param name goal name
     * @param targetAmount target amount to reach
     * @param savedAmount initial saved amount
     * @param deadline deadline of the goal
     */
    public Goal(int userId, String name, float targetAmount, float savedAmount, String deadline) {
        this.userId = userId;
        this.name = name;
        this.targetAmount = targetAmount;
        this.savedAmount = savedAmount;
        this.deadline = deadline;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public float getTargetAmount() { return targetAmount; }
    public void setTargetAmount(float targetAmount) { this.targetAmount = targetAmount; }
    public float getSavedAmount() { return savedAmount; }
    public void setSavedAmount(float savedAmount) { this.savedAmount = savedAmount; }
    public String getDeadline() { return deadline; }
    public void setDeadline(String deadline) { this.deadline = deadline; }
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
    /**
     * Calculates the progress percentage toward the goal.
     *
     * @return progress percentage (max 100%)
     */
    public double getProgressPercent() {
        if (targetAmount <= 0) return 0;
        return Math.min((savedAmount / targetAmount) * 100.0, 100.0);
    }
    /**
     * Calculates the remaining amount needed to reach the goal.
     *
     * @return remaining amount
     */
    public float getRemaining() {
        return Math.max(targetAmount - savedAmount, 0);
    }
}
