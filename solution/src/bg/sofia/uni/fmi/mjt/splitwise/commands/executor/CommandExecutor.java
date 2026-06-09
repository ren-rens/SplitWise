package bg.sofia.uni.fmi.mjt.splitwise.commands.executor;

import bg.sofia.uni.fmi.mjt.splitwise.commands.Command;
import bg.sofia.uni.fmi.mjt.splitwise.commands.constants.CommandConstants;
import bg.sofia.uni.fmi.mjt.splitwise.commands.executor.validator.ExecutorValidator;
import bg.sofia.uni.fmi.mjt.splitwise.server.Session;
import bg.sofia.uni.fmi.mjt.splitwise.server.repositories.splitwise.SplitWiseRepository;

public class CommandExecutor {

    public static String execute(SplitWiseRepository repository, Command cmd, Session session) {
        /**
         * These validations are not needed because the implementation does not suggest a user would be able
         * to send null input but not checking the reference before using it is not good practice :)
         **/
        if (!ExecutorValidator.validateRepository(repository) ||
            !ExecutorValidator.validateCommand(cmd)
            || !ExecutorValidator.validateSession(session)) {
            return "{\"status\":\"ERROR\",\"message\":\"Unknown error occurred\"}";
        }

        return switch (cmd.command()) {
            case CommandConstants.REGISTRATION -> RegistrationExecutor.execute(repository, cmd.arguments(), session);
            case CommandConstants.LOGIN -> LoginExecutor.execute(repository, cmd.arguments(), session);
            case CommandConstants.ADD_FRIEND -> AddFriendExecutor.execute(repository, cmd.arguments(), session);
            case CommandConstants.CREATE_GROUP -> CreateGroupExecutor.execute(repository, cmd.arguments(), session);
            case CommandConstants.SPLIT -> SplitWithFriendExecutor.execute(repository, cmd.arguments(), session);
            case CommandConstants.SPLIT_GROUP -> SplitWithGroupExecutor.execute(repository, cmd.arguments(), session);
            case CommandConstants.GET_STATUS -> GetStatusExecutor.execute(repository, session);
            case CommandConstants.PAY -> PayExecutor.execute(repository, cmd.arguments(), session);
            case CommandConstants.HELP -> HelpExecutor.execute();
            case CommandConstants.CHECK_HISTORY -> CheckHistoryExecutor.execute(repository, session);
            case CommandConstants.LOGOUT -> LogoutExecutor.execute(session);
            case CommandConstants.GET_FRIENDS -> GetFriendsExecutor.execute(repository, session);
            case CommandConstants.GET_GROUPS -> GetGroupsExecutor.execute(repository, session);
            default -> "{\"status\":\"ERROR\",\"message\":\"Unknown error occurred\"}";
        };
    }

}
