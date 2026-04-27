package com.fakkaflow.logic.service;

import com.fakkaflow.data.model.User;

public class SessionManager {
    private static SessionManager instance;
    private User currentUser;
    private String jwtToken;

    private SessionManager() {}

    public static SessionManager getInstance() {
        if (instance == null) instance = new SessionManager();
        return instance;
    }

    public String createSession(int userId) {
        jwtToken = "session_" + userId + "_" + System.currentTimeMillis();
        return jwtToken;
    }

    public void setCurrentUser(User user) { this.currentUser = user; }
    public User getCurrentUser() { return currentUser; }
    public int getCurrentUserId() { return currentUser != null ? currentUser.getId() : -1; }
    public boolean isLoggedIn() { return currentUser != null; }

    public void logout() {
        currentUser = null;
        jwtToken = null;
    }
}
