package com.example.myapplication.data.validation;

import android.os.PatternMatcher;
import android.util.Log;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DataFieldsValidation {

    private final String validEmail = "^(?!.*(example\\.com|test\\.com|dummy\\.com|sample\\.com|mailinator\\.com)$)([a-zA-Z0-9._%+-]+)@([a-zA-Z0-9.-]+\\.[a-zA-Z]{2,})$\n";
    private final String validPhoneNumber = "^(?:\\+639|09)\\d{9}$\n";

    public DataFieldsValidation() {

    }

    /**
     * It is simply validate the email by comparing if the email will match to the given regex.
     * @param email is the user must provide to create a account or to login.

     */
    public String validateEmail(String email) {
        email = email.strip();
        Pattern pattern = Pattern.compile(this.validEmail);
        Matcher matcher = pattern.matcher(email);
        String message = "";
        if(!matcher.matches()){
            Log.d("Debug", "Invalid Email address");
            // return this message if invalid email address
            message = "Invalid email address";
            return message;
        }
        Log.d("Debug", "Valid Email address");
        //return empty string if it is valid email
        return message;

    }

    public static boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) return false;
        String regex = "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,}$";
        return email.trim().toUpperCase().matches(regex);
    }

    public String validatePhoneNumber(String phoneNumber) {
        phoneNumber = phoneNumber.strip();
        Pattern pattern = Pattern.compile(this.validPhoneNumber);
        Matcher matcher = pattern.matcher(phoneNumber);
        String message = "";
        if(!matcher.matches()){
            Log.d("Debug", "Invalid phone number");
            // return this message if invalid email address
            message = "Invalid phone number";
            return message;
        }
        Log.d("Debug", "Invalid phone number");
        //return empty string if it is valid email
        return message;

    }

    /**
     * It is simply validate the password by the given conditions.
     * @param password is the user must provide to create a account or to login.
     * @return String, which is the message will display in the screen.

     */
    public String validatePassword(String password){
        password = password.strip();

        String message = "";
        if (password.length() < 8) {
            return "Too short (minimum 8 characters required)";
        }

        if (password.length() > 32) {
            return "Too long (maximum 32 characters allowed)";
        }

        // Pattern checks
        if (!password.matches(".*[a-z].*")) {
            message = "Must contain at least one lowercase letter\n";
            return message;
        }
        if (!password.matches(".*[A-Z].*")) {
            message = "Must contain at least one uppercase letter\n";
            return message;
        }
        if (!password.matches(".*\\d.*")) {
            message = "Must contain at least one digit\n";
            return message;
        }
        if (!password.matches(".*[@$!%*?&].*")) {
            message = "Must contain at least one special character (@, $, !, %, *, ?, &)\n";
            return message;
        }
        if (password.matches(".*\\s.*")) {
            message = "Should not contain spaces\n";
            return message;
        }

        return message;
    }


    /**
     * It is simply validate the field if it is empty or not.
     * @param dataField is a field where the data will be inputted.
     * @return String, which is the message will display in the screen.

     */
    public boolean isEmptyField(String dataField){
        dataField = dataField.strip();
        return dataField.isEmpty();

    }

    /**
     * It is simply check whether the password and confirm password is match.
     * @param password is the user must provide to create a account or to login
     * @param confirmPassword must be equals to the password.

     */
    public boolean isPasswordMatch(String password, String confirmPassword){
        password = password.strip();
        confirmPassword = confirmPassword.strip();

        return password.equals(confirmPassword);
    }

}