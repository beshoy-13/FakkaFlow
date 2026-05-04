package com.fakkaflow.data.model;
/**
 * Represents a user in the system.
 * Stores authentication and profile information.
 */

public class User {
    private int id;
    private String name;
    private String email;
    private String passwordHash;
    private String createdAt;


    /**
     * Default constructor.
     */
    public User() {}
    /**
     * Creates a new user.
     *
     * @param id user ID
     * @param name user name
     * @param email user email
     * @param passwordHash hashed password
     * @param createdAt account creation timestamp
     */
    public User(int id, String name, String email, String passwordHash, String createdAt) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.passwordHash = passwordHash;
        this.createdAt = createdAt;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() { return name + " <" + email + ">"; }
}
