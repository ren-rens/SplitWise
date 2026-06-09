package bg.sofia.uni.fmi.mjt.splitwise.exceptions;

public class UserAlreadyHasFriendWithSuchUsernameException extends Exception {

    public UserAlreadyHasFriendWithSuchUsernameException(String message) {
        super(message);
    }

    public UserAlreadyHasFriendWithSuchUsernameException(String message, Throwable cause) {
        super(message, cause);
    }

}
