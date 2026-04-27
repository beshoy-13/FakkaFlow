package com.fakkaflow.data.repository;

import java.sql.*;

public class SQLiteDatabase {
    private static final String DB_URL = "jdbc:sqlite:fakkaflow.db";
    private static SQLiteDatabase instance;
    private Connection connection;

    private SQLiteDatabase() {}

    public static SQLiteDatabase getInstance() {
        if (instance == null) instance = new SQLiteDatabase();
        return instance;
    }

    public void open() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(DB_URL);
            connection.setAutoCommit(true);
        }
    }

    public void close() {
        try {
            if (connection != null && !connection.isClosed()) connection.close();
        } catch (SQLException ignored) {}
    }

    public ResultSet query(String sql, Object... params) throws SQLException {
        PreparedStatement stmt = connection.prepareStatement(sql);
        for (int i = 0; i < params.length; i++) stmt.setObject(i + 1, params[i]);
        return stmt.executeQuery();
    }

    public int execute(String sql, Object... params) throws SQLException {
        PreparedStatement stmt = connection.prepareStatement(sql);
        for (int i = 0; i < params.length; i++) stmt.setObject(i + 1, params[i]);
        return stmt.executeUpdate();
    }

    public long executeInsert(String sql, Object... params) throws SQLException {
        PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        for (int i = 0; i < params.length; i++) stmt.setObject(i + 1, params[i]);
        stmt.executeUpdate();
        ResultSet keys = stmt.getGeneratedKeys();
        return keys.next() ? keys.getLong(1) : -1;
    }

    public void initializeSchema() {
        try {
            open();
            execute("""
                CREATE TABLE IF NOT EXISTS users (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    name TEXT NOT NULL,
                    email TEXT UNIQUE NOT NULL,
                    password_hash TEXT NOT NULL,
                    created_at TEXT DEFAULT (datetime('now'))
                )""");
            execute("""
                CREATE TABLE IF NOT EXISTS categories (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    name TEXT UNIQUE NOT NULL
                )""");
            execute("""
                CREATE TABLE IF NOT EXISTS transactions (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    user_id INTEGER NOT NULL,
                    amount REAL NOT NULL,
                    type TEXT NOT NULL,
                    category_id INTEGER,
                    note TEXT,
                    timestamp TEXT DEFAULT (datetime('now')),
                    FOREIGN KEY(user_id) REFERENCES users(id),
                    FOREIGN KEY(category_id) REFERENCES categories(id)
                )""");
            execute("""
                CREATE TABLE IF NOT EXISTS budget_cycles (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    user_id INTEGER NOT NULL,
                    total_allowance REAL NOT NULL,
                    start_date TEXT NOT NULL,
                    end_date TEXT NOT NULL,
                    FOREIGN KEY(user_id) REFERENCES users(id)
                )""");
            execute("""
                CREATE TABLE IF NOT EXISTS budgets (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    user_id INTEGER NOT NULL,
                    category_id INTEGER NOT NULL,
                    amount_limit REAL NOT NULL,
                    period TEXT NOT NULL,
                    FOREIGN KEY(user_id) REFERENCES users(id),
                    FOREIGN KEY(category_id) REFERENCES categories(id)
                )""");
            execute("""
                CREATE TABLE IF NOT EXISTS goals (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    user_id INTEGER NOT NULL,
                    name TEXT NOT NULL,
                    target_amount REAL NOT NULL,
                    saved_amount REAL DEFAULT 0,
                    deadline TEXT,
                    created_at TEXT DEFAULT (datetime('now')),
                    FOREIGN KEY(user_id) REFERENCES users(id)
                )""");
            seedCategories();
        } catch (SQLException e) {
            throw new RuntimeException("DB init failed: " + e.getMessage(), e);
        }
    }

    private void seedCategories() throws SQLException {
        String[] defaults = {"Food", "Transport", "Shopping", "Health", "Entertainment", "Utilities", "Salary", "Other"};
        for (String cat : defaults) {
            execute("INSERT OR IGNORE INTO categories (name) VALUES (?)", cat);
        }
    }

    public Connection getConnection() { return connection; }
}
