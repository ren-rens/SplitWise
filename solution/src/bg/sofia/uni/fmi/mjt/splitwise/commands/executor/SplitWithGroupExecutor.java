package bg.sofia.uni.fmi.mjt.splitwise.commands.executor;

import bg.sofia.uni.fmi.mjt.splitwise.commands.constants.ExecutorConstants;
import bg.sofia.uni.fmi.mjt.splitwise.commands.executor.validator.ExecutorValidator;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.GroupWithThisNameDoesNotExistException;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.NoFriendWithSuchUsernameExistsException;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.UserWithThisUsernameDoesNotExistException;
import bg.sofia.uni.fmi.mjt.splitwise.server.Session;
import bg.sofia.uni.fmi.mjt.splitwise.server.model.requests.UserSplitRequest;
import bg.sofia.uni.fmi.mjt.splitwise.server.repositories.splitwise.SplitWiseRepository;

public class SplitWithGroupExecutor {

    public static String execute(SplitWiseRepository service, String[] arguments, Session session) {
        String validator = ExecutorValidator.validateSplitWithGroup(arguments, session);
        if (validator != null) {
            return validator;
        }

        return splitWithGroupImpl(service, arguments, session.getUsername());
    }

    private static String splitWithGroupImpl(SplitWiseRepository service, String[] arguments, String loggedUser) {
        String groupName = arguments[ExecutorConstants.ONE_IDX];
        String reasonForPayment = arguments[ExecutorConstants.TWO_IDX];

        try {

            double amount = Double.parseDouble(arguments[ExecutorConstants.ZERO_IDX]);
            if (!ExecutorValidator.validateAmountNotNull(amount)) {
                return "{\"status\":\"ERROR\",\"message\":\"The amount must be a positive number.\"}";
            }

            UserSplitRequest request = new UserSplitRequest(loggedUser, amount, groupName, reasonForPayment, true);
            service.split(request);

        } catch (UserWithThisUsernameDoesNotExistException e) {
            return String.format("{\"status\":\"ERROR\",\"message\":\"User %s does not exist.\"}",
                loggedUser);
        } catch (GroupWithThisNameDoesNotExistException e) {
            return String.format("{\"status\":\"ERROR\",\"message\":\"Group %s does not exist.\"}",
                groupName);
        } catch (NoFriendWithSuchUsernameExistsException e) {
            return "{\"status\":\"ERROR\",\"message\":\"Not all users in the group exist.\"}";
        } catch (NumberFormatException e) {
            return "{\"status\":\"ERROR\",\"message\":\"The given amount could not be parsed to a double number.\"}";
        }

        return String.format("{\"status\":\"OK\",\"message\":\"User %s split dept with group successfully.\"}",
            loggedUser);
    }

}
