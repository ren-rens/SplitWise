package bg.sofia.uni.fmi.mjt.splitwise.commands.executor;

import bg.sofia.uni.fmi.mjt.splitwise.commands.constants.CommandConstants;
import bg.sofia.uni.fmi.mjt.splitwise.server.Session;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class LogoutExecutorTest {

    @Test
    void testLogoutWithLoggedInUser() {
        String username = "username";
        Session session = new Session();
        session.login(username);

        assertEquals(CommandConstants.DISCONNECT, LogoutExecutor.execute(session),
            "When logging out should return disconnect message that reaches the client" +
                "to disconnect from the server");

        assertFalse(session.isLoggedIn(), "After logging out session should be expired");
    }

    @Test
    void testLogoutWithLoggedOutUser() {
        Session session = new Session();

        assertEquals(CommandConstants.DISCONNECT, LogoutExecutor.execute(session),
            "When logging out but the user is already logged out" +
                "should just return disconnect message that reaches the client" +
                "to disconnect from the server");

        assertFalse(session.isLoggedIn(), "After logging out session should be expired");
    }

}
