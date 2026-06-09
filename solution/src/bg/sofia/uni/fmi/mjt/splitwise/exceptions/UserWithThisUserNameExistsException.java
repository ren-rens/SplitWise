package bg.sofia.uni.fmi.mjt.splitwise.exceptions;

public class UserWithThisUserNameExistsException extends Exception {

    public UserWithThisUserNameExistsException(String message) {
        super(message);
    }

    public UserWithThisUserNameExistsException(String message, Throwable cause) {
        super(message, cause);
    }

}
