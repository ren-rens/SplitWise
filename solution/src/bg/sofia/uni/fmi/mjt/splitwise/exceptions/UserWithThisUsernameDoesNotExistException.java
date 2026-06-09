package bg.sofia.uni.fmi.mjt.splitwise.exceptions;

public class UserWithThisUsernameDoesNotExistException extends Exception {

    public UserWithThisUsernameDoesNotExistException(String message) {
        super(message);
    }

    public UserWithThisUsernameDoesNotExistException(String message, Throwable cause) {
        super(message, cause);
    }

}
