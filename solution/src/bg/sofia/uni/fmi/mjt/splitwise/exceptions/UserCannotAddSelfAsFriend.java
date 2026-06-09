package bg.sofia.uni.fmi.mjt.splitwise.exceptions;

public class UserCannotAddSelfAsFriend extends Exception {

    public UserCannotAddSelfAsFriend(String message) {
        super(message);
    }

    public UserCannotAddSelfAsFriend(String message, Throwable cause) {
        super(message, cause);
    }

}
