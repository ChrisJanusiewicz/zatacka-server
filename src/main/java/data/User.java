package data;


import java.time.LocalDateTime;

public class User {

    private long userID;
    private String userName;
    private byte[] passwordHash;
    private byte[] salt;
    private LocalDateTime lastLogin;
    private LocalDateTime registeredAt;

    public User(long userID, String userName, byte[] passwordHash, byte[] salt, LocalDateTime lastLogin, LocalDateTime registeredAt) {
        this.userID = userID;
        this.userName = userName;
        this.passwordHash = passwordHash;
        this.salt = salt;
        this.lastLogin = lastLogin;
        this.registeredAt = registeredAt;
    }

    public long getUserID() {
        return userID;
    }

    public String getUserName() {
        return userName;
    }

    public byte[] getPasswordHash() {
        return passwordHash;
    }

    public LocalDateTime getLastLogin() {
        return lastLogin;
    }

    public LocalDateTime getRegisteredAt() {
        return registeredAt;
    }

    public byte[] getSalt() {
        return salt;
    }


}
