package layout.Interface;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class User {
    private String email;
    private String username;
    private char[] password;  // Storing password as a char array for security reasons
    private boolean isOneTimePassword;
    private LocalDateTime passwordExpiry;  // When one-time password becomes invalid
    private String firstName;
    private String middleName;
    private String lastName;
    private String preferredName;

    // Enum to represent expertise levels
    public enum ExpertiseLevel {
        BEGINNER, INTERMEDIATE, ADVANCED, EXPERT
    }
    
    private ExpertiseLevel expertiseLevel = ExpertiseLevel.INTERMEDIATE;  // Default value

    // Constructor
    public User(String email, String username, char[] password, boolean isOneTimePassword, 
                LocalDateTime passwordExpiry, String firstName, String middleName, 
                String lastName, String preferredName) {
        this.email = email;
        this.username = username;
        this.password = password;
        this.isOneTimePassword = isOneTimePassword;
        this.passwordExpiry = passwordExpiry;
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.preferredName = preferredName;
    }
    
    public String getEmail(){
        return email;
    }
    public void setEmail(String email){
        this.email = email;
    }
    public String getUsername(){
        return username;
    }
    public void setUsername(String username){
        this.username = username;
    }
    public char[] getPassword(){
        return password;
    }
    public void setPassword(char[] password){
        this.password = password;
    }
    public boolean isOneTimePassword(){
        return isOneTimePassword;
    }
    public void setOneTimePassword(boolean oneTimePassword) {
        isOneTimePassword = oneTimePassword;
    }
    public LocalDateTime getPasswordExpiry(){
        return passwordExpiry;
    }
    public void setPasswordExpiry(LocalDateTime passwordExpiry) {
        this.passwordExpiry = passwordExpiry;
    }
    public String getFirstName(){
        return firstName;
    }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    public String getMiddleName(){
        return middleName;
    }
    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getLastName() {
        return lastName;
    }
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPreferredName(){
        return preferredName;
    }
    public void setPreferredName(String preferredName) {
        this.preferredName = preferredName;
    }
    public ExpertiseLevel getExpertiseLevel(){
        return expertiseLevel;
    }
    public void setExpertiseLevel(ExpertiseLevel expertiseLevel) {
        this.expertiseLevel = expertiseLevel;
    }
}
