package Interface;

public class InviteCode {

    private String code;

    // Constructor to initialize the invite code
    public InviteCode(String code) {
        this.code = code;
    }

    // Getter for the invite code
    public String getCode() {
        return code;
    }

    // Setter for the invite code
    public void setCode(String code) {
        this.code = code;
    }

    // Method to validate the invite code
    // Add your own validation logic here, e.g., check against a database or predefined codes
    public boolean isValidCode() {
        // Example logic: let's assume "12345" is a valid invite code
        if (code != null && code.equals("12345")) {
            return true;
        } else {
            return false;
        }
    }

    // Other potential methods could include:
    // - Save invite code to the database
    // - Check if the invite code has expired, etc.
}
