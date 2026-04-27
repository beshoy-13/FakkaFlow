package com.fakkaflow.data.repository;

import com.fakkaflow.data.model.User;
import java.sql.*;

public class UserRepository {
    private final SQLiteDatabase db = SQLiteDatabase.getInstance();

    public User findByEmail(String email) {
        try {
            ResultSet rs = db.query("SELECT * FROM users WHERE email = ?", email);
            if (rs.next()) return mapRow(rs);
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    public long save(User user) {
        try {
            return db.executeInsert(
                "INSERT INTO users (name, email, password_hash) VALUES (?, ?, ?)",
                user.getName(), user.getEmail(), user.getPasswordHash()
            );
        } catch (SQLException e) { e.printStackTrace(); return -1; }
    }

    public User findById(int id) {
        try {
            ResultSet rs = db.query("SELECT * FROM users WHERE id = ?", id);
            if (rs.next()) return mapRow(rs);
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

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
