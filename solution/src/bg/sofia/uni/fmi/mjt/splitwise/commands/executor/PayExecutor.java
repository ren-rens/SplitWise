package bg.sofia.uni.fmi.mjt.splitwise.commands.executor;

import bg.sofia.uni.fmi.mjt.splitwise.commands.constants.ExecutorConstants;
import bg.sofia.uni.fmi.mjt.splitwise.commands.executor.validator.ExecutorValidator;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.GroupWithThisNameDoesNotExistException;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.NoFriendWithSuchUsernameExistsException;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.UserWithThisUsernameDoesNotExistException;
import bg.sofia.uni.fmi.mjt.splitwise.server.Session;
import bg.sofia.uni.fmi.mjt.splitwise.server.repositories.splitwise.SplitWiseRepository;

public class PayExecutor {

    public static String execute(SplitWiseRepository service, String[] arguments, Session session) {
        String validator = ExecutorValidator.validatePay(arguments, session);
        if (validator != null) {
            return validator;
        }

        return payImpl(service, arguments, session.getUsername());
    }

    private static String payImpl(SplitWiseRepository repository, String[] arguments, String loggedUser) {
        try {
            if (!payExecution(repository, arguments, loggedUser)) {
                return "{\"status\":\"ERROR\",\"message\":\"Given amount should be more than zero\"}";
            }
        } catch (NumberFormatException e) {
            return "{\"status\":\"ERROR\",\"message\":\"Given amount is not a double number\"}";
        } catch (UserWithThisUsernameDoesNotExistException e) {
            return String.format("{\"status\":\"ERROR\",\"message\":\"User %s does not exist.\"}",
                loggedUser);
        } catch (NoFriendWithSuchUsernameExistsException e) {
            return "{\"status\":\"ERROR\",\"message\":\"Friend does not exist.\"}";
        } catch (GroupWithThisNameDoesNotExistException e) {
            return "{\"status\":\"ERROR\",\"message\":\"Group does not exist.\"}";
        }

        return String.format("{\"status\":\"OK\",\"message\":\"Friend successfully paid user %s.\"}",
            loggedUser);
    }

    private static boolean payExecution(SplitWiseRepository repository, String[] arguments, String loggedUser)
        throws NoFriendWithSuchUsernameExistsException, GroupWithThisNameDoesNotExistException,
        UserWithThisUsernameDoesNotExistException {
        String groupName = null;
        double amount;
        String friendUsername;

        if (arguments.length == ExecutorConstants.PAY_FRIEND_ARGUMENTS_COUNT) {
            amount = Double.parseDouble(arguments[ExecutorConstants.ZERO_IDX]);
            friendUsername = arguments[ExecutorConstants.ONE_IDX];
        } else {
            amount = Double.parseDouble(arguments[ExecutorConstants.ZERO_IDX]);
            groupName = arguments[ExecutorConstants.ONE_IDX];
            friendUsername = arguments[ExecutorConstants.TWO_IDX];
        }
        if (!ExecutorValidator.validateAmountNotNull(amount)) {
            return false;
        }

        repository.payed(loggedUser, groupName, amount, friendUsername);
        return true;
    }

}
