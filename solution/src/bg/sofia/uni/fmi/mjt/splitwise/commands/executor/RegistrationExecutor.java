package bg.sofia.uni.fmi.mjt.splitwise.commands.executor;

import bg.sofia.uni.fmi.mjt.splitwise.commands.constants.ExecutorConstants;
import bg.sofia.uni.fmi.mjt.splitwise.commands.executor.validator.ExecutorValidator;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.UserDataMustContainAtLeastOneNonBlankSymbolException;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.UserWithThisUserNameExistsException;
import bg.sofia.uni.fmi.mjt.splitwise.server.Session;
import bg.sofia.uni.fmi.mjt.splitwise.server.repositories.splitwise.SplitWiseRepository;

public class RegistrationExecutor {

    public static String execute(SplitWiseRepository service, String[] arguments, Session session) {
        String validator = ExecutorValidator.validateRegistration(arguments, session);
        if (validator != null) {
            return validator;
        }

        return registerUserImpl(service, arguments);
    }

    private static String registerUserImpl(SplitWiseRepository service, String[] arguments) {
        String username = arguments[ExecutorConstants.ZERO_IDX];
        String password = arguments[ExecutorConstants.ONE_IDX];
        try {
            service.registerUser(username, password);
        } catch (UserWithThisUserNameExistsException e) {
            return String.format("{\"status\":\"ERROR\",\"message\":\"User %s already exists.\"}",
                username);
        } catch (UserDataMustContainAtLeastOneNonBlankSymbolException e) {
            return "{\"status\":\"ERROR\",\"message\":\"Invalid user data: \"}" + e.getMessage();
        }

        return String.format("{\"status\":\"OK\",\"message\":\"User %s created successfully.\"}",
            username);
    }

}
