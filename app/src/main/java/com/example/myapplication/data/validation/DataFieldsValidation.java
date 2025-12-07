package com.example.myapplication.data.validation;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

public class DataFieldsValidation {

    private final String validEmailRegex = "^(?!.*(example\\.com|test\\.com|dummy\\.com|sample\\.com|mailinator\\.com)$)([a-zA-Z0-9._%+-]+)@([a-zA-Z0-9.-]+\\.[a-zA-Z]{2,})$\n";
    // allows optional spaces/hyphens/dots/parentheses between digits
    // Full set of valid PH mobile prefixes as of 2025 (NTC/Telco official)
    private static final Set<String> VALID_PH_PREFIXES = new HashSet<>(Set.of(
            // Globe/TM (Primary)
            "0904", "0905", "0906", "0915", "0916", "0917", "0926", "0927", "0935", "0936", "0937",
            "0945", "0950", "0951", "0955", "0956", "0961", "0965", "0966", "0967", "0970", "0973",
            "0974", "0975", "0977", "0978", "0979", "0981", "0989", "0994", "0995", "0997", "0998",
            // Smart/TNT/Sun
            "0900", "0907", "0908", "0909", "0910", "0911", "0912", "0913", "0914", "0918", "0919",
            "0920", "0921", "0922", "0923", "0924", "0925", "0928", "0929", "0930", "0931", "0932",
            "0933", "0934", "0938", "0939", "0940", "0941", "0942", "0943", "0946", "0947", "0948", "0949",
            "0952", "0953", "0954", "0960", "0962", "0963", "0964", "0968", "0969", "0971", "0972", "0976",
            "0980", "0982", "0983", "0984", "0985", "0986", "0987", "0988",
            // DITO (Newer entrant)
            "0895", "0896", "0897", "0898", "0991", "0992", "0993"
    ));
    private static final Pattern PH_MOBILE_PATTERN = Pattern.compile("^09\\d{9}$");
    public DataFieldsValidation() {

    }


    public String getErrorMessage(String password) {
        // Check if password contains at least one letter and one number
        boolean hasLetter = password.matches(".*[a-zA-Z].*");
        boolean hasNumber = password.matches(".*\\d.*");

        boolean isEightCharacter = password.length() > 8;

        if (!hasLetter) return "Must contain letters.";
        if (!isEightCharacter) return "New password must at least 8 characters.";
        if (!hasNumber) return "Must contain numbers.";
        return "";
    }
    public boolean isFieldValid(String fullName){
        if (fullName == null) return false;
        fullName = fullName.trim();
        if (fullName.isEmpty()) return false;

        // Unicode letters and spaces only, must start with a letter
        String regex = "^[\\p{L}]+(?:[ \\p{L}]+)*$";
        return fullName.matches(regex);
    }
    public boolean isStreetPurokValid(String fullName){
        if (fullName == null) return false;
        fullName = fullName.trim();
        if (fullName.isEmpty()) return false;

        String regex = "^[\\p{L}\\p{N} ]+$";
        return fullName.matches(regex);
    }

    public boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) return false;
        String regex = "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,}$";
        return email.trim().toUpperCase().matches(regex);
    }
    public boolean isValidPHMobile(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            return false;
        }
        String cleaned = phone.replaceAll("[^0-9+]", "");
        if (cleaned.startsWith("+63")) {
            cleaned = "0" + cleaned.substring(3);
        } else if (cleaned.startsWith("63")) {
            cleaned = "0" + cleaned.substring(2);
        } else if (cleaned.length() == 10 && cleaned.startsWith("9")) {
            cleaned = "0" + cleaned;
        }

        // Must be exactly 11 digits starting with 09
        if (!PH_MOBILE_PATTERN.matcher(cleaned).matches()) {
            return false;
        }

        // Check against valid prefixes
        String prefix = cleaned.substring(0, 4);
        return VALID_PH_PREFIXES.contains(prefix);
    }

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