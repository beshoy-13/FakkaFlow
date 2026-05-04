package com.fakkaflow.data.repository;

import com.fakkaflow.data.model.UserSettings;
import java.sql.*;
/**
 * Repository class responsible for managing user settings in the database.
 *
 * Provides methods to load and save user preferences such as
 * currency, language, and notification settings.
 */
public class UserSettingsRepository {
    private final SQLiteDatabase db = SQLiteDatabase.getInstance();
    /**
     * Loads user settings from the database.
     *
     * If no settings exist, default settings are returned.
     *
     * @param userId ID of the user
     * @return UserSettings object containing preferences
     */
    public UserSettings load(int userId) {
        try {
            ResultSet rs = db.query("SELECT * FROM user_settings WHERE user_id=?", userId);
            if (rs.next()) {
                UserSettings s = new UserSettings();
                s.setId(rs.getInt("id"));
                s.setUserId(rs.getInt("user_id"));
                s.setCurrency(rs.getString("currency"));
                s.setLanguage(rs.getString("language"));
                s.setNotificationsEnabled(rs.getInt("notifications_enabled") == 1);
                return s;
            }
        } catch (SQLException e) { e.printStackTrace(); }
        UserSettings defaults = new UserSettings();
        defaults.setUserId(userId);
        return defaults;
    }
    /**
     * Saves user settings to the database.
     *
     * If settings already exist, they are updated.
     * Otherwise, a new record is inserted.
     *
     * @param s UserSettings object to save
     */
    public void save(UserSettings s) {
        try {
            ResultSet existing = db.query("SELECT id FROM user_settings WHERE user_id=?", s.getUserId());
            if (existing.next()) {
                db.execute(
                    "UPDATE user_settings SET currency=?, language=?, notifications_enabled=? WHERE user_id=?",
                    s.getCurrency(), s.getLanguage(), s.isNotificationsEnabled() ? 1 : 0, s.getUserId()
                );
            } else {
                db.executeInsert(
                    "INSERT INTO user_settings (user_id, currency, language, notifications_enabled) VALUES (?,?,?,?)",
                    s.getUserId(), s.getCurrency(), s.getLanguage(), s.isNotificationsEnabled() ? 1 : 0
                );
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }
}
