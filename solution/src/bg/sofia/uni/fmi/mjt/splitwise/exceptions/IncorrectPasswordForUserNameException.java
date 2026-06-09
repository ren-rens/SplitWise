package bg.sofia.uni.fmi.mjt.splitwise.exceptions;

public class IncorrectPasswordForUserNameException extends Exception {

    public IncorrectPasswordForUserNameException(String message) {
        super(message);
    }

    public IncorrectPasswordForUserNameException(String message, Throwable cause) {
        super(message, cause);
    }

}
