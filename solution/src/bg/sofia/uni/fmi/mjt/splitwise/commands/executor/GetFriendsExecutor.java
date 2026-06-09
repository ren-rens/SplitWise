package bg.sofia.uni.fmi.mjt.splitwise.commands.executor;

import bg.sofia.uni.fmi.mjt.splitwise.commands.executor.validator.ExecutorValidator;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.UserWithThisUsernameDoesNotExistException;
import bg.sofia.uni.fmi.mjt.splitwise.server.Session;
import bg.sofia.uni.fmi.mjt.splitwise.server.repositories.splitwise.SplitWiseRepository;

public class GetFriendsExecutor {

    public static String execute(SplitWiseRepository repository, Session session) {
        String loginValidator = ExecutorValidator.validateIsUserLoggedIn(session);
        if (loginValidator != null) {
            return loginValidator;
        }

        return getFriendsImpl(repository, session.getUsername());
    }

    private static String getFriendsImpl(SplitWiseRepository repository, String loggedUsername) {
        try {
            return repository.getFriendsOfUser(loggedUsername);
        } catch (UserWithThisUsernameDoesNotExistException e) {
            return String.format("{\"status\":\"ERROR\",\"message\":\"User %s does not exist.\"}",
                loggedUsername);
        }
    }

}
