package com.fakkaflow.data.repository;

import com.fakkaflow.data.model.Budget;
import java.sql.*;
import java.util.*;

/**
 * Repository class for managing Budget entities in the database.
 * Provides CRUD operations for budgets.
 */
public class BudgetRepository {
    private final SQLiteDatabase db = SQLiteDatabase.getInstance();
    /**
     * Saves a budget.
     * If a budget already exists for the same user and category,
     * it updates the existing record; otherwise, it inserts a new one.
     *
     * @param budget the budget to save
     */
    public void save(Budget budget) {
        try {
            ResultSet existing = db.query(
                "SELECT id FROM budgets WHERE user_id=? AND category_id=?",
                budget.getUserId(), budget.getCategoryId()
            );
            if (existing.next()) {
                db.execute(
                    "UPDATE budgets SET amount_limit=?, period=? WHERE user_id=? AND category_id=?",
                    budget.getAmountLimit(), budget.getPeriod(), budget.getUserId(), budget.getCategoryId()
                );
            } else {
                db.executeInsert(
                    "INSERT INTO budgets (user_id, category_id, amount_limit, period) VALUES (?,?,?,?)",
                    budget.getUserId(), budget.getCategoryId(), budget.getAmountLimit(), budget.getPeriod()
                );
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }
    /**
     * Retrieves all budgets for a specific user.
     *
     * @param userId the user ID
     * @return list of budgets
     */
    public List<Budget> findAll(int userId) {
        List<Budget> list = new ArrayList<>();
        try {
            ResultSet rs = db.query(
                "SELECT b.*, c.name as cat_name FROM budgets b JOIN categories c ON b.category_id=c.id WHERE b.user_id=?",
                userId
            );
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }
    /**
     * Deletes a budget by its ID.
     *
     * @param budgetId the budget ID
     */
    public void delete(int budgetId) {
        try {
            db.execute("DELETE FROM budgets WHERE id=?", budgetId);
        } catch (SQLException e) { e.printStackTrace(); }
    }
    /**
     * Maps a database row to a Budget object.
     *
     * @param rs the result set
     * @return mapped Budget object
     * @throws SQLException if database error occurs
     */
    private Budget mapRow(ResultSet rs) throws SQLException {
        Budget b = new Budget();
        b.setId(rs.getInt("id"));
        b.setUserId(rs.getInt("user_id"));
        b.setCategoryId(rs.getInt("category_id"));
        b.setCategoryName(rs.getString("cat_name"));
        b.setAmountLimit(rs.getFloat("amount_limit"));
        b.setPeriod(rs.getString("period"));
        return b;
    }
}
