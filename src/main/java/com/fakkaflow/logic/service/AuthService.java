package com.fakkaflow.logic.service;

import com.fakkaflow.data.model.User;
import com.fakkaflow.data.repository.UserRepository;
import org.mindrot.jbcrypt.BCrypt;

public class AuthService {
    private final UserRepository userRepository = new UserRepository();
    private final ValidationService validationService = new ValidationService();
    private final SessionManager sessionManager = SessionManager.getInstance();

    public User registerUser(String name, String email, String password) throws Exception {
        if (!validationService.validateName(name))
            throw new Exception("Name cannot be empty.");
        if (!validationService.validateEmail(email))
            throw new Exception("Invalid email format.");
        if (!validationService.validatePasswordStrength(password))
            throw new Exception("Password must be at least 6 characters.");
        if (userRepository.findByEmail(email) != null)
            throw new Exception("An account with this email already exists.");

        String hash = BCrypt.hashpw(password, BCrypt.gensalt());
        User user = new User();
        user.setName(name.trim());
        user.setEmail(email.trim().toLowerCase());
        user.setPasswordHash(hash);

        long userId = userRepository.save(user);
        if (userId < 0) throw new Exception("Registration failed. Please try again.");

        user.setId((int) userId);
        sessionManager.setCurrentUser(user);
        sessionManager.createSession(user.getId());
        return user;
    }

    public User loginUser(String email, String password) throws Exception {
        if (!validationService.validateEmail(email))
            throw new Exception("Invalid email format.");
        if (password == null || password.isEmpty())
            throw new Exception("Password cannot be empty.");

        User user = userRepository.findByEmail(email.trim().toLowerCase());
        if (user == null || !BCrypt.checkpw(password, user.getPasswordHash()))
            throw new Exception("Invalid email or password.");

        sessionManager.setCurrentUser(user);
        sessionManager.createSession(user.getId());
        return user;
    }

    public void logout() {
        sessionManager.logout();
    }
}
