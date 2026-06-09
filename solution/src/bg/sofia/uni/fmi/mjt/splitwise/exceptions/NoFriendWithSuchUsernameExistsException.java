package bg.sofia.uni.fmi.mjt.splitwise.exceptions;

public class NoFriendWithSuchUsernameExistsException extends Exception {

    public NoFriendWithSuchUsernameExistsException(String message) {
        super(message);
    }

    public NoFriendWithSuchUsernameExistsException(String message, Throwable cause) {
        super(message, cause);
    }

}
