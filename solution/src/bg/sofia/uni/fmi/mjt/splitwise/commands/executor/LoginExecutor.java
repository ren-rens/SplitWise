package bg.sofia.uni.fmi.mjt.splitwise.commands.executor;

import bg.sofia.uni.fmi.mjt.splitwise.commands.constants.ExecutorConstants;
import bg.sofia.uni.fmi.mjt.splitwise.commands.executor.validator.ExecutorValidator;
import bg.sofia.uni.fmi.mjt.splitwise.server.Session;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.IncorrectPasswordForUserNameException;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.UserWithThisUsernameDoesNotExistException;
import bg.sofia.uni.fmi.mjt.splitwise.server.repositories.splitwise.SplitWiseRepository;

public class LoginExecutor {

    public static String execute(SplitWiseRepository service, String[] arguments, Session session) {
        String validator = ExecutorValidator.validateLoginUser(arguments, session);
        if (validator != null) {
            return validator;
        }

        return loginUserImpl(service, arguments, session);
    }

    private static String loginUserImpl(SplitWiseRepository service, String[] arguments, Session session) {
        String username = arguments[ExecutorConstants.ZERO_IDX];
        String password = arguments[ExecutorConstants.ONE_IDX];

        try {
            String notifications = System.lineSeparator() + service.loginUser(username, password, session);

            return String.format("{\"status\":\"OK\",\"message\":\"User %s logged in successfully. %s\"}",
                username, notifications);
        } catch (IncorrectPasswordForUserNameException e) {
            return String.format("{\"status\":\"ERROR\",\"message\":\"User %s with such password does not exist.\"}",
                username);
        } catch (UserWithThisUsernameDoesNotExistException e) {
            return String.format("{\"status\":\"ERROR\",\"message\":\"User %s does not exist.\"}",
                username);
        }
    }

}
