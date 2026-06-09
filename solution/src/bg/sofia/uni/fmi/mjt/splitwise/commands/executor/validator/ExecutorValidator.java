package bg.sofia.uni.fmi.mjt.splitwise.commands.executor.validator;

import bg.sofia.uni.fmi.mjt.splitwise.commands.Command;
import bg.sofia.uni.fmi.mjt.splitwise.commands.constants.ExecutorConstants;
import bg.sofia.uni.fmi.mjt.splitwise.server.Session;
import bg.sofia.uni.fmi.mjt.splitwise.server.model.payments.debt.DebtConstants;
import bg.sofia.uni.fmi.mjt.splitwise.server.repositories.splitwise.SplitWiseRepository;

public class ExecutorValidator {

    public static boolean validateRepository(SplitWiseRepository repository) {
        return repository != null;
    }

    public static boolean validateCommand(Command cmd) {
        return cmd != null &&
            cmd.command() != null && !cmd.command().isBlank() &&
            cmd.arguments() != null;
    }

    public static boolean validateSession(Session session) {
        return session != null;
    }

    public static String validateAddFriend(String[] arguments, Session session) {
        if (!ExecutorValidator.validateExecutor(arguments, ExecutorConstants.ADD_FRIEND_ARGUMENTS_COUNT)) {
            return "{\"status\":\"ERROR\",\"message\":" +
                "\"Usage:add-friend <username> \"}";
        }

        return validateIsUserLoggedIn(session);
    }

    public static String validateCreateGroup(String[] arguments, Session session) {
        if (arguments.length < ExecutorConstants.CREATE_GROUP_ARGUMENTS_COUNT) {
            return "{\"status\":\"ERROR\",\"message\":\"Usage: create-group at least 3 arguments needed\"}";
        }

        return validateIsUserLoggedIn(session);
    }

    public static String validateLoginUser(String[] arguments, Session session) {
        if (!ExecutorValidator.validateExecutor(arguments,
            ExecutorConstants.LOGIN_ARGUMENTS_COUNT)) {
            return "{\"status\":\"ERROR\",\"message\":" +
                "\"Usage:login <username> <password>\"}";
        }

        if (session.isLoggedIn()) {
            return "{\"status\":\"ERROR\",\"message\":\"Already logged in\"}";
        }

        return null;
    }

    public static String validatePay(String[] arguments, Session session) {
        if (!ExecutorValidator.validateExecutor(arguments,
            ExecutorConstants.PAY_FRIEND_ARGUMENTS_COUNT) &&
            !ExecutorValidator.validateExecutor(arguments,
                ExecutorConstants.PAY_FRIEND_FROM_GROUP_ARGUMENTS_COUNT)) {
            return "{\"status\":\"ERROR\",\"message\":" +
                "\"Usage:pay (<group>) <amount> <username>\"}";
        }

        return validateIsUserLoggedIn(session);
    }

    public static String validateRegistration(String[] arguments, Session session) {
        if (!ExecutorValidator.validateExecutor(arguments,
            ExecutorConstants.REGISTRATION_ARGUMENTS_COUNT)) {
            return "{\"status\":\"ERROR\",\"message\":" +
                "\"Usage:registration <username> <password>\"}";
        }

        if (session.isLoggedIn()) {
            return "{\"status\":\"ERROR\",\"message\":\"Already logged in\"}";
        }

        return null;
    }

    public static String validateSplitWithFriends(String[] arguments, Session session) {
        if (!ExecutorValidator.validateExecutor(arguments, ExecutorConstants.SPLIT_ARGUMENTS_COUNT)) {
            return "{\"status\":\"ERROR\",\"message\":" +
                "\"Usage: split <amount> <username> <reason-for-payment>\"}";
        }

        return validateIsUserLoggedIn(session);
    }

    public static String validateSplitWithGroup(String[] arguments, Session session) {
        if (!ExecutorValidator.validateExecutor(arguments, ExecutorConstants.SPLIT_ARGUMENTS_COUNT)) {
            return "{\"status\":\"ERROR\",\"message\":" +
                "\"Usage: split-group <amount> <group_name> <reason-for-payment>\"}";
        }

        return validateIsUserLoggedIn(session);
    }

    public static String validateIsUserLoggedIn(Session session) {
        if (!isUserLoggedIn(session)) {
            return "{\"status\":\"ERROR\",\"message\":" +
                "\"Usage: Session expired\"}";
        }

        return null;
    }

    public static boolean validateAmountNotNull(double amount) {
        return amount > DebtConstants.ZERO_DEBT;
    }

    private static boolean validateExecutor(String[] arguments, int neededSize) {
        return arguments != null && arguments.length == neededSize;
    }

    private static boolean isUserLoggedIn(Session session) {
        return session.isLoggedIn();
    }

}