package bg.sofia.uni.fmi.mjt.splitwise.exceptions;

public class GroupNamesMustContainAtLeastOneNonBlankSymbolException extends Exception {

    public GroupNamesMustContainAtLeastOneNonBlankSymbolException(String message) {
        super(message);
    }

    public GroupNamesMustContainAtLeastOneNonBlankSymbolException(String message, Throwable cause) {
        super(message, cause);
    }

}
