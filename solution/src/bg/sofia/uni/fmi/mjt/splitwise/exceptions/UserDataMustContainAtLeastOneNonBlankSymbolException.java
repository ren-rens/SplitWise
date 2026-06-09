package bg.sofia.uni.fmi.mjt.splitwise.exceptions;

public class UserDataMustContainAtLeastOneNonBlankSymbolException extends Exception {

    public UserDataMustContainAtLeastOneNonBlankSymbolException(String message, Throwable cause) {
        super(message, cause);
    }

    public UserDataMustContainAtLeastOneNonBlankSymbolException(String message) {
        super(message);
    }

}
