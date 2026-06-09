package bg.sofia.uni.fmi.mjt.splitwise.commands.executor;

import bg.sofia.uni.fmi.mjt.splitwise.commands.constants.ExecutorConstants;
import bg.sofia.uni.fmi.mjt.splitwise.commands.executor.validator.ExecutorValidator;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.UserCannotAddSelfAsFriend;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.UserDataMustContainAtLeastOneNonBlankSymbolException;
import bg.sofia.uni.fmi.mjt.splitwise.server.Session;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.UserAlreadyHasFriendWithSuchUsernameException;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.UserWithThisUsernameDoesNotExistException;
import bg.sofia.uni.fmi.mjt.splitwise.server.repositories.splitwise.SplitWiseRepository;

public class AddFriendExecutor {

    public static String execute(SplitWiseRepository service, String[] arguments, Session session) {
        String validator = ExecutorValidator.validateAddFriend(arguments, session);
        if (validator != null) {
            return validator;
        }

        return addFriendImpl(service, arguments, session.getUsername());
    }

    private static String addFriendImpl(SplitWiseRepository service, String[] arguments, String loggedUser) {
        String friendUsername = arguments[ExecutorConstants.ZERO_IDX];

        try {
            service.addFriend(loggedUser, friendUsername);
        } catch (UserWithThisUsernameDoesNotExistException e) {
            return String.format("{\"status\":\"ERROR\",\"message\":\"%s.\"}",
                e.getMessage());
        } catch (UserAlreadyHasFriendWithSuchUsernameException e) {
            return String.format("{\"status\":\"ERROR\",\"message\":\"User %s has a friend with this username.\"}",
                loggedUser);
        } catch (UserCannotAddSelfAsFriend e) {
            return String.format("{\"status\":\"ERROR\",\"message\":\"User %s cannot add self as a friend.\"}",
                loggedUser);
        } catch (UserDataMustContainAtLeastOneNonBlankSymbolException e) {
            return String.format("{\"status\":\"ERROR\",\"message\":\"User data error: %s.\"}",
                e.getMessage());
        }

        return String.format("{\"status\":\"OK\",\"message\":\"User %s added %s as a friend.\"}",
            loggedUser, friendUsername);
    }

}
