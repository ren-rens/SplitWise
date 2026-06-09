package bg.sofia.uni.fmi.mjt.splitwise.exceptions;

public class NotEnoughUsersToCreateAGroupException extends Exception {

    public NotEnoughUsersToCreateAGroupException(String message) {
        super(message);
    }

    public NotEnoughUsersToCreateAGroupException(String message, Throwable cause) {
        super(message, cause);
    }

}
