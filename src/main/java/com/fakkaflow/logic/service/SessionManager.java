package com.fakkaflow.logic.service;

import com.fakkaflow.data.model.User;

/**
 * Singleton class responsible for managing user sessions.
 * Keeps track of the currently logged-in user and session token.
 */
public class SessionManager {
    private static SessionManager instance;
    private User currentUser;
    private String jwtToken;

    /**
     * Private constructor to enforce singleton pattern.
     */
    private SessionManager() {}
    /**
     * Returns the single instance of SessionManager.
     *
     * @return SessionManager instance
     */
    public static SessionManager getInstance() {
        if (instance == null) instance = new SessionManager();
        return instance;
    }
    /**
     * Creates a session token for a user.
     *
     * @param userId user ID
     * @return generated session token
     */
    public String createSession(int userId) {
        jwtToken = "session_" + userId + "_" + System.currentTimeMillis();
        return jwtToken;
    }
    /**
     * Sets the currently logged-in user.
     *
     * @param user user object
     */
    public void setCurrentUser(User user) { this.currentUser = user; }
    /**
     * Returns the current user.
     *
     * @return current User or null
     */
    public User getCurrentUser() { return currentUser; }
    /**
     * Returns the current user ID.
     *
     * @return user ID or -1 if no user logged in
     */
    public int getCurrentUserId() { return currentUser != null ? currentUser.getId() : -1; }

    /**
     * Checks if a user is logged in.
     *
     * @return true if logged in, false otherwise
     */
    public boolean isLoggedIn() { return currentUser != null; }

    /**
     * Logs out the current user and clears session data.
     */
    public void logout() {
        currentUser = null;
        jwtToken = null;
    }
}
