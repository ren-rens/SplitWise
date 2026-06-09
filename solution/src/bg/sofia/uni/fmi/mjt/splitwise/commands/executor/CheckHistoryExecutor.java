package bg.sofia.uni.fmi.mjt.splitwise.commands.executor;

import bg.sofia.uni.fmi.mjt.splitwise.commands.executor.validator.ExecutorValidator;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.UserWIthThisUsernameDoesNotHaveAnyPaymentHistory;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.UserWithThisUsernameDoesNotExistException;
import bg.sofia.uni.fmi.mjt.splitwise.server.Session;
import bg.sofia.uni.fmi.mjt.splitwise.server.repositories.splitwise.SplitWiseRepository;

public class CheckHistoryExecutor {

    public static String execute(SplitWiseRepository service, Session session) {
        String validator = ExecutorValidator.validateIsUserLoggedIn(session);
        if (validator != null) {
            return validator;
        }

        return checkHistoryImpl(service, session.getUsername());
    }

    private static String checkHistoryImpl(SplitWiseRepository service, String loggedUser) {
        try {
            return service.checkHistory(loggedUser);
        } catch (UserWithThisUsernameDoesNotExistException | UserWIthThisUsernameDoesNotHaveAnyPaymentHistory e) {
            return "{\"status\":\"ERROR\",\"message\":\"" + e.getMessage() + "\"}";
        }
    }

}
