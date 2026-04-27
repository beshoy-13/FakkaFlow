package com.fakkaflow.logic.service;

import java.time.LocalDate;
import java.util.regex.Pattern;

public class ValidationService {
    private static final Pattern EMAIL_PATTERN =
        Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    public boolean validateEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email.trim()).matches();
    }

    public boolean validatePasswordStrength(String password) {
        return password != null && password.length() >= 6;
    }

    public boolean validateAmount(float amount) {
        return amount > 0;
    }

    public boolean validateAmountString(String amountStr) {
        try {
            float val = Float.parseFloat(amountStr.trim());
            return validateAmount(val);
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public boolean validateDateRange(LocalDate start, LocalDate end) {
        return start != null && end != null && !end.isBefore(start);
    }

    public boolean validateName(String name) {
        return name != null && !name.trim().isEmpty();
    }
}
