package com.fakkaflow.logic.service;

import java.time.LocalDate;
import java.util.regex.Pattern;

/**
 * Service responsible for validating user input such as
 * email, password, amounts, and dates.
 */
public class ValidationService {

    private static final Pattern EMAIL_PATTERN =
        Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    /**
     * Validates email format using regex pattern.
     *
     * @param email email string
     * @return true if valid, false otherwise
     */
    public boolean validateEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email.trim()).matches();
    }
    /**
     * Validates password strength.
     * Minimum length is 6 characters.
     *
     * @param password password string
     * @return true if valid, false otherwise
     */
    public boolean validatePasswordStrength(String password) {
        return password != null && password.length() >= 6;
    }
    /**
     * Validates numeric amount.
     *
     * @param amount numeric value
     * @return true if greater than zero
     */
    public boolean validateAmount(float amount) {
        return amount > 0;
    }
    /**
     * Validates amount provided as string.
     *
     * @param amountStr string representation of amount
     * @return true if valid number and greater than zero
     */
    public boolean validateAmountString(String amountStr) {
        try {
            float val = Float.parseFloat(amountStr.trim());
            return validateAmount(val);
        } catch (NumberFormatException e) {
            return false;
        }
    }
    /**
     * Validates date range.
     *
     * @param start start date
     * @param end end date
     * @return true if both dates are valid and end is not before start
     */
    public boolean validateDateRange(LocalDate start, LocalDate end) {
        return start != null && end != null && !end.isBefore(start);
    }
    /**
     * Validates user name.
     *
     * @param name user name
     * @return true if not null or empty
     */
    public boolean validateName(String name) {
        return name != null && !name.trim().isEmpty();
    }
}
