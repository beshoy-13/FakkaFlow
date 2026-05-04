package com.fakkaflow.logic.service;

import com.fakkaflow.data.model.Goal;
import com.fakkaflow.data.repository.GoalRepository;
import java.util.List;
/**
 * Service responsible for managing financial goals.
 */
public class GoalService {
    /**
     * Saves a goal after validation.
     */
    private final GoalRepository goalRepository = new GoalRepository();

    public void saveGoal(Goal goal) throws Exception {
        if (goal.getName() == null || goal.getName().trim().isEmpty())
            throw new Exception("Goal name cannot be empty.");
        if (goal.getTargetAmount() <= 0)
            throw new Exception("Target amount must be greater than zero.");
        goalRepository.save(goal);
    }
    /**
     * Adds contribution to a goal.
     */
    public void addContribution(int goalId, float contribution) throws Exception {
        if (contribution <= 0) throw new Exception("Contribution must be greater than zero.");
        goalRepository.updateSavedAmount(goalId, contribution);
    }

    public void deleteGoal(int goalId) {
        goalRepository.delete(goalId);
    }

    public List<Goal> getGoals(int userId) {
        return goalRepository.findAll(userId);
    }

    /**
     * Calculates required monthly saving.
     */
    public float calculateMonthlySavingsNeeded(Goal goal) {
        if (goal.getDeadline() == null || goal.getDeadline().isEmpty()) return 0;
        try {
            java.time.LocalDate deadline = java.time.LocalDate.parse(goal.getDeadline());
            long months = java.time.temporal.ChronoUnit.MONTHS.between(java.time.LocalDate.now(), deadline);
            if (months <= 0) return goal.getRemaining();
            return goal.getRemaining() / months;
        } catch (Exception e) { return 0; }
    }
}
