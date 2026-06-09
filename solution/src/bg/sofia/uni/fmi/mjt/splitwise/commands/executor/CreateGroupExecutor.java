package bg.sofia.uni.fmi.mjt.splitwise.commands.executor;

import bg.sofia.uni.fmi.mjt.splitwise.commands.constants.ExecutorConstants;
import bg.sofia.uni.fmi.mjt.splitwise.commands.executor.validator.ExecutorValidator;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.GroupNamesMustContainAtLeastOneNonBlankSymbolException;
import bg.sofia.uni.fmi.mjt.splitwise.server.Session;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.GroupWithThisNameAlreadyExistsException;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.NotEnoughUsersToCreateAGroupException;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.UserWithThisUsernameDoesNotExistException;
import bg.sofia.uni.fmi.mjt.splitwise.server.repositories.splitwise.SplitWiseRepository;

public class CreateGroupExecutor {

    public static String execute(SplitWiseRepository repository, String[] arguments, Session session) {
        String validator = ExecutorValidator.validateCreateGroup(arguments, session);
        if (validator != null) {
            return validator;
        }

        return createGroupImpl(repository, arguments, session.getUsername());
    }

    private static String createGroupImpl(SplitWiseRepository repository, String[] arguments, String loggedUser) {
        String groupName = arguments[ExecutorConstants.ZERO_IDX];
        String[] usernames = getUserNames(arguments);

        try {
            repository.createGroup(loggedUser, groupName, usernames);
        } catch (GroupWithThisNameAlreadyExistsException e) {
            return String.format("{\"status\":\"ERROR\",\"message\":\"Group %s already exists.\"}",
                groupName);
        } catch (NotEnoughUsersToCreateAGroupException e) {
            return String.format("{\"status\":\"ERROR\",\"message\":\"Group %s cannot be created with users.\"}",
                groupName);
        } catch (UserWithThisUsernameDoesNotExistException e) {
            return String.format("{\"status\":\"ERROR\",\"message\":\"%s.\"}",
                e.getMessage());
        } catch (GroupNamesMustContainAtLeastOneNonBlankSymbolException e) {
            return String.format("{\"status\":\"ERROR\",\"message\":\"Group name error occurred: %s.\"}",
                e.getMessage());
        }

        return String.format("{\"status\":\"OK\",\"message\":\"Group %s created successfully.\"}",
            groupName);
    }

    private static String[] getUserNames(String[] arguments) {
        String[] result = new String[arguments.length - 1];
        boolean isGroupNamePassed = false;
        int idx = 0;

        for (String argument : arguments) {
            if (!isGroupNamePassed) {
                isGroupNamePassed = true;
                continue;
            }

            result[idx++] = argument;
        }

        return result;
    }

}
