package bg.sofia.uni.fmi.mjt.splitwise.commands.executor;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HelpExecutorTest {

    @Test
    void testHelpExecutor() {
        String expected = "registration <username> <password>" + System.lineSeparator() +
            "login <username> <password>" + System.lineSeparator() +
            "add-friend <username>" + System.lineSeparator() +
            "create-group <group_name> <username> <username> ... <username>" + System.lineSeparator() +
            "split <amount> <username> <reason_for_payment>" + System.lineSeparator() +
            "split-group <amount> <group_name> <reason_for_payment>" + System.lineSeparator() +
            "get-status" + System.lineSeparator() +
            "payed <amount> <username>" + System.lineSeparator() +
            "check-history" + System.lineSeparator() +
            "get-friends" + System.lineSeparator() +
            "get-groups" + System.lineSeparator();

        assertEquals(expected, HelpExecutor.execute(),
            "Should just return the available commands in the system that the client could use");
    }

}
