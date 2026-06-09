package bg.sofia.uni.fmi.mjt.splitwise.commands.executor;

import bg.sofia.uni.fmi.mjt.splitwise.commands.executor.validator.ExecutorValidator;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.UserWithThisUsernameDoesNotExistException;
import bg.sofia.uni.fmi.mjt.splitwise.server.Session;
import bg.sofia.uni.fmi.mjt.splitwise.server.repositories.splitwise.SplitWiseRepository;

public class GetStatusExecutor {

    public static String execute(SplitWiseRepository service, Session session) {
        String validator = ExecutorValidator.validateIsUserLoggedIn(session);
        if (validator != null) {
            return validator;
        }

        return getStatusImpl(service, session.getUsername());
    }

    private static String getStatusImpl(SplitWiseRepository service, String loggedUser) {
        try {
            return service.getStatus(loggedUser);
        } catch (UserWithThisUsernameDoesNotExistException e) {
            return String.format("{\"status\":\"ERROR\",\"message\":\"User %s does not exist.\"}",
                    loggedUser);
        }
    }

}
