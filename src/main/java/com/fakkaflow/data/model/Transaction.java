package com.fakkaflow.data.model;
/**
 * Represents a financial transaction (income or expense).
 *
 * Each transaction is linked to a user and a category,
 * and includes amount, type, and timestamp.
 */
public class Transaction {
    private int transactionId;
    private int userId;
    private float amount;
    private String type;
    private int categoryId;
    private String categoryName;
    private String note;
    private String timestamp;
    /**
     * Default constructor.
     */
    public Transaction() {}
    /**
     * Creates a new transaction.
     *
     * @param userId ID of the user
     * @param amount transaction amount
     * @param type type of transaction (income or expense)
     * @param categoryId category ID
     * @param note optional note
     */
    public Transaction(int userId, float amount, String type, int categoryId, String note) {
        this.userId = userId;
        this.amount = amount;
        this.type = type;
        this.categoryId = categoryId;
        this.note = note;
    }

    public int getTransactionId() { return transactionId; }
    public void setTransactionId(int transactionId) { this.transactionId = transactionId; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public float getAmount() { return amount; }
    public void setAmount(float amount) { this.amount = amount; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public int getCategoryId() { return categoryId; }
    public void setCategoryId(int categoryId) { this.categoryId = categoryId; }
    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }
    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
}
