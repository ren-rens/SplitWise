package bg.sofia.uni.fmi.mjt.splitwise.exceptions;

public class UserWIthThisUsernameDoesNotHaveAnyPaymentHistory extends Exception {

    public UserWIthThisUsernameDoesNotHaveAnyPaymentHistory(String message) {
        super(message);
    }

    public UserWIthThisUsernameDoesNotHaveAnyPaymentHistory(String message, Throwable cause) {
        super(message, cause);
    }

}