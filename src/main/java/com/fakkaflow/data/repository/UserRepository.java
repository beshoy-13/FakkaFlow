package com.fakkaflow.data.repository;

import com.fakkaflow.data.model.User;
import java.sql.*;
/**
 * Repository class for managing User entities.
 */
public class UserRepository {
    private final SQLiteDatabase db = SQLiteDatabase.getInstance();
    /**
     * Finds user by email.
     *
     * @param email user email
     * @return User or null
     */

    public User findByEmail(String email) {
        try {
            ResultSet rs = db.query("SELECT * FROM users WHERE email = ?", email);
            if (rs.next()) return mapRow(rs);
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }
    /**
     * Saves a new user.
     *
     * @param user user object
     * @return generated ID or -1 if failed
     */
    public long save(User user) {
        try {
            return db.executeInsert(
                "INSERT INTO users (name, email, password_hash) VALUES (?, ?, ?)",
                user.getName(), user.getEmail(), user.getPasswordHash()
            );
        } catch (SQLException e) { e.printStackTrace(); return -1; }
    }
    /**
     * Finds user by ID.
     */
    public User findById(int id) {
        try {
            ResultSet rs = db.query("SELECT * FROM users WHERE id = ?", id);
            if (rs.next()) return mapRow(rs);
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }
    /**
     * Maps row to User object.
     */
    private User mapRow(ResultSet rs) throws SQLException {
        User u = new User();
        u.setId(rs.getInt("id"));
        u.setName(rs.getString("name"));
        u.setEmail(rs.getString("email"));
        u.setPasswordHash(rs.getString("password_hash"));
        u.setCreatedAt(rs.getString("created_at"));
        return u;
    }
}
