package bg.sofia.uni.fmi.mjt.splitwise.exceptions;

public class GroupWithThisNameDoesNotExistException extends Exception {

    public GroupWithThisNameDoesNotExistException(String message) {
        super(message);
    }

    public GroupWithThisNameDoesNotExistException(String message, Throwable cause) {
        super(message, cause);
    }

}
