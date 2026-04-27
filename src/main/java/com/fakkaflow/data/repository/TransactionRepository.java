package com.fakkaflow.data.repository;

import com.fakkaflow.data.model.Transaction;
import java.sql.*;
import java.util.*;

public class TransactionRepository {
    private final SQLiteDatabase db = SQLiteDatabase.getInstance();

    public void save(Transaction t) {
        try {
            db.executeInsert(
                "INSERT INTO transactions (user_id, amount, type, category_id, note) VALUES (?, ?, ?, ?, ?)",
                t.getUserId(), t.getAmount(), t.getType(), t.getCategoryId(), t.getNote()
            );
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public void update(Transaction t) {
        try {
            db.execute(
                "UPDATE transactions SET amount=?, type=?, category_id=?, note=? WHERE id=?",
                t.getAmount(), t.getType(), t.getCategoryId(), t.getNote(), t.getTransactionId()
            );
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public void delete(int transactionId) {
        try {
            db.execute("DELETE FROM transactions WHERE id=?", transactionId);
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public List<Transaction> findAll(int userId) {
        List<Transaction> list = new ArrayList<>();
        try {
            ResultSet rs = db.query(
                "SELECT t.*, c.name as cat_name FROM transactions t LEFT JOIN categories c ON t.category_id = c.id WHERE t.user_id=? ORDER BY t.timestamp DESC",
                userId
            );
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public List<Transaction> findByFilter(int userId, String type, Integer categoryId, String startDate, String endDate) {
        StringBuilder sql = new StringBuilder(
            "SELECT t.*, c.name as cat_name FROM transactions t LEFT JOIN categories c ON t.category_id = c.id WHERE t.user_id=?"
        );
        List<Object> params = new ArrayList<>();
        params.add(userId);
        if (type != null && !type.isEmpty()) { sql.append(" AND t.type=?"); params.add(type); }
        if (categoryId != null && categoryId > 0) { sql.append(" AND t.category_id=?"); params.add(categoryId); }
        if (startDate != null && !startDate.isEmpty()) { sql.append(" AND t.timestamp >= ?"); params.add(startDate); }
        if (endDate != null && !endDate.isEmpty()) { sql.append(" AND t.timestamp <= ?"); params.add(endDate + " 23:59:59"); }
        sql.append(" ORDER BY t.timestamp DESC");
        List<Transaction> list = new ArrayList<>();
        try {
            ResultSet rs = db.query(sql.toString(), params.toArray());
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public float getTotalByTypeAndUser(int userId, String type) {
        try {
            ResultSet rs = db.query(
                "SELECT COALESCE(SUM(amount), 0) as total FROM transactions WHERE user_id=? AND type=?",
                userId, type
            );
            if (rs.next()) return rs.getFloat("total");
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    public float getSpentByCategory(int userId, int categoryId) {
        try {
            ResultSet rs = db.query(
                "SELECT COALESCE(SUM(amount), 0) as total FROM transactions WHERE user_id=? AND category_id=? AND type='expense'",
                userId, categoryId
            );
            if (rs.next()) return rs.getFloat("total");
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    public Map<String, Float> getSpentByCategory(int userId) {
        Map<String, Float> map = new LinkedHashMap<>();
        try {
            ResultSet rs = db.query(
                "SELECT c.name, COALESCE(SUM(t.amount),0) as total FROM transactions t JOIN categories c ON t.category_id=c.id WHERE t.user_id=? AND t.type='expense' GROUP BY c.name ORDER BY total DESC",
                userId
            );
            while (rs.next()) map.put(rs.getString("name"), rs.getFloat("total"));
        } catch (SQLException e) { e.printStackTrace(); }
        return map;
    }

    private Transaction mapRow(ResultSet rs) throws SQLException {
        Transaction t = new Transaction();
        t.setTransactionId(rs.getInt("id"));
        t.setUserId(rs.getInt("user_id"));
        t.setAmount(rs.getFloat("amount"));
        t.setType(rs.getString("type"));
        t.setCategoryId(rs.getInt("category_id"));
        t.setCategoryName(rs.getString("cat_name"));
        t.setNote(rs.getString("note"));
        t.setTimestamp(rs.getString("timestamp"));
        return t;
    }
}
