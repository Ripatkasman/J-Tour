package j.tour.model;

public abstract class User {
    protected String userId;
    protected String username;
    protected String passwordHash;

    public User(String userId, String username, String passwordHash) {
        this.userId = userId;
        this.username = username;
        this.passwordHash = passwordHash;
    }

    public String getUsername() { return username; }
    public String getUserId() { return userId; }
}