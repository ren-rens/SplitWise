package bg.sofia.uni.fmi.mjt.splitwise.commands.executor;

import bg.sofia.uni.fmi.mjt.splitwise.commands.executor.validator.ExecutorValidator;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.UserWithThisUsernameDoesNotExistException;
import bg.sofia.uni.fmi.mjt.splitwise.server.Session;
import bg.sofia.uni.fmi.mjt.splitwise.server.repositories.splitwise.SplitWiseRepository;

public class GetGroupsExecutor {

    public static String execute(SplitWiseRepository repository, Session session) {
        String loginValidator = ExecutorValidator.validateIsUserLoggedIn(session);
        if (loginValidator != null) {
            return loginValidator;
        }

        return getGroupsImpl(repository, session.getUsername());
    }

    private static String getGroupsImpl(SplitWiseRepository repository, String loggedUsername) {
        try {
            return repository.getGroupOfUser(loggedUsername);
        } catch (UserWithThisUsernameDoesNotExistException e) {
            return String.format("{\"status\":\"ERROR\",\"message\":\"User %s does not exist.\"}",
                loggedUsername);
        }
    }

}
