package com.fakkaflow.data.repository;

import com.fakkaflow.data.model.Goal;
import java.sql.*;
import java.util.*;
/**
 * Repository class for managing Goal entities.
 */
public class GoalRepository {
    private final SQLiteDatabase db = SQLiteDatabase.getInstance();

    /**
     * Inserts a new goal.
     *
     * @param goal goal object
     */
    public void save(Goal goal) {
        try {
            db.executeInsert(
                "INSERT INTO goals (user_id, name, target_amount, saved_amount, deadline) VALUES (?,?,?,?,?)",
                goal.getUserId(), goal.getName(), goal.getTargetAmount(), goal.getSavedAmount(), goal.getDeadline()
            );
        } catch (SQLException e) { e.printStackTrace(); }
    }
    /**
     * Updates saved amount for a goal.
     *
     * @param goalId goal ID
     * @param contribution amount to add
     */
    public void updateSavedAmount(int goalId, float contribution) {
        try {
            db.execute(
                "UPDATE goals SET saved_amount = saved_amount + ? WHERE id=?",
                contribution, goalId
            );
        } catch (SQLException e) { e.printStackTrace(); }
    }
    /**
     * Deletes a goal.
     *
     * @param goalId goal ID
     */
    public void delete(int goalId) {
        try {
            db.execute("DELETE FROM goals WHERE id=?", goalId);
        } catch (SQLException e) { e.printStackTrace(); }
    }

    /**
     * Retrieves all goals for a user.
     *
     * @param userId user ID
     * @return list of goals
     */
    public List<Goal> findAll(int userId) {
        List<Goal> list = new ArrayList<>();
        try {
            ResultSet rs = db.query(
                "SELECT * FROM goals WHERE user_id=? ORDER BY created_at DESC", userId
            );
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }
    /**
     * Maps a result set row to a Goal object.
     */
    private Goal mapRow(ResultSet rs) throws SQLException {
        Goal g = new Goal();
        g.setId(rs.getInt("id"));
        g.setUserId(rs.getInt("user_id"));
        g.setName(rs.getString("name"));
        g.setTargetAmount(rs.getFloat("target_amount"));
        g.setSavedAmount(rs.getFloat("saved_amount"));
        g.setDeadline(rs.getString("deadline"));
        g.setCreatedAt(rs.getString("created_at"));
        return g;
    }
}
