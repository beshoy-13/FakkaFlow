package com.fakkaflow.data.repository;

import com.fakkaflow.data.model.BudgetCycle;
import java.sql.*;
import java.time.LocalDate;
/**
 * Repository class for managing BudgetCycle entities.
 */
public class CycleRepository {
    private final SQLiteDatabase db = SQLiteDatabase.getInstance();
    /**
     * Saves a budget cycle for a user.
     * Deletes any existing cycle before inserting a new one.
     *
     * @param cycle budget cycle object
     */
    public void save(BudgetCycle cycle) {
        try {
            db.execute("DELETE FROM budget_cycles WHERE user_id=?", cycle.getUserId());
            db.executeInsert(
                "INSERT INTO budget_cycles (user_id, total_allowance, start_date, end_date) VALUES (?,?,?,?)",
                cycle.getUserId(), cycle.getTotalAllowance(),
                cycle.getStartDate().toString(), cycle.getEndDate().toString()
            );
        } catch (SQLException e) { e.printStackTrace(); }
    }
    /**
     * Loads the latest budget cycle for a user.
     *
     * @param userId user ID
     * @return BudgetCycle or null if not found
     */
    public BudgetCycle load(int userId) {
        try {
            ResultSet rs = db.query(
                "SELECT * FROM budget_cycles WHERE user_id=? ORDER BY id DESC LIMIT 1", userId
            );
            if (rs.next()) {
                BudgetCycle c = new BudgetCycle();
                c.setCycleId(rs.getInt("id"));
                c.setUserId(rs.getInt("user_id"));
                c.setTotalAllowance(rs.getFloat("total_allowance"));
                c.setStartDate(LocalDate.parse(rs.getString("start_date")));
                c.setEndDate(LocalDate.parse(rs.getString("end_date")));
                return c;
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }
    /**
     * Deletes a user's budget cycle.
     *
     * @param userId user ID
     */
    public void delete(int userId) {
        try {
            db.execute("DELETE FROM budget_cycles WHERE user_id=?", userId);
        } catch (SQLException e) { e.printStackTrace(); }
    }
}
