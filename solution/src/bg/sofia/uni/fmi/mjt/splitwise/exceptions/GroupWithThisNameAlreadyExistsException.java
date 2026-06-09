package bg.sofia.uni.fmi.mjt.splitwise.exceptions;

public class GroupWithThisNameAlreadyExistsException extends Exception {

    public GroupWithThisNameAlreadyExistsException(String message) {
        super(message);
    }

    public GroupWithThisNameAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }

}
