package bg.sofia.uni.fmi.mjt.splitwise.server;

public class Session {

    public boolean isLoggedIn() {
        return this.username != null;
    }

    public String getUsername() {
        return this.username;
    }

    public void login(String username) {
        this.username = username;
    }

    public void logout() {
        this.username = null;
    }


    private String username;

}
