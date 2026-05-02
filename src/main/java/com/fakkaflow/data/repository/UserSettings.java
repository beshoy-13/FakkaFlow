package com.fakkaflow.data.model;

public class UserSettings {
    private int id;
    private int userId;
    private String currency;
    private String language;
    private boolean notificationsEnabled;

    public UserSettings() {
        this.currency = "EGP";
        this.language = "English";
        this.notificationsEnabled = true;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
    public String getLanguage() { return language; }
    public void setLanguage(String language) { this.language = language; }
    public boolean isNotificationsEnabled() { return notificationsEnabled; }
    public void setNotificationsEnabled(boolean v) { this.notificationsEnabled = v; }
}
