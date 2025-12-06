package com.example.myapplication.data.validation;

public class DataFieldsValidation {

    private final String validEmailRegex = "^(?!.*(example\\.com|test\\.com|dummy\\.com|sample\\.com|mailinator\\.com)$)([a-zA-Z0-9._%+-]+)@([a-zA-Z0-9.-]+\\.[a-zA-Z]{2,})$\n";
    // allows optional spaces/hyphens/dots/parentheses between digits
    private final String validPhoneNumberRegex ="^(?:09|\\+639|639)(?:[ .\\-()]?\\d){9}$";
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

    public boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) return false;
        String regex = "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,}$";
        return email.trim().toUpperCase().matches(regex);
    }
    public boolean isValidPHMobile(String phone) {
        return phone != null && phone.replaceAll("\\D", "").matches("^09\\d{9}$");
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