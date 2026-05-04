package com.fakkaflow.data.repository;

import com.fakkaflow.data.model.Category;
import java.sql.*;
import java.util.*;
/**
 * Repository class for managing Category entities.
 */
public class CategoryRepository {
    private final SQLiteDatabase db = SQLiteDatabase.getInstance();
    /**
     * Retrieves all categories ordered by name.
     *
     * @return list of categories
     */
    public List<Category> findAll() {
        List<Category> list = new ArrayList<>();
        try {
            ResultSet rs = db.query("SELECT * FROM categories ORDER BY name");
            while (rs.next()) {
                list.add(new Category(rs.getInt("id"), rs.getString("name")));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }
    /**
     * Inserts a category if it does not already exist.
     *
     * @param name category name
     */
    public void save(String name) {
        try {
            db.execute("INSERT OR IGNORE INTO categories (name) VALUES (?)", name);
        } catch (SQLException e) { e.printStackTrace(); }
    }
}
