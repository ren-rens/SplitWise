package bg.sofia.uni.fmi.mjt.splitwise.commands.executor;

import bg.sofia.uni.fmi.mjt.splitwise.commands.constants.CommandConstants;
import bg.sofia.uni.fmi.mjt.splitwise.server.Session;

public class LogoutExecutor {

    public static String execute(Session session) {
        session.logout();

        return CommandConstants.DISCONNECT;
    }

}
