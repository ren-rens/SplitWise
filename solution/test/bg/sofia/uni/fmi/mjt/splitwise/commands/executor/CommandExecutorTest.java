package bg.sofia.uni.fmi.mjt.splitwise.commands.executor;

import bg.sofia.uni.fmi.mjt.splitwise.commands.Command;
import bg.sofia.uni.fmi.mjt.splitwise.commands.constants.CommandConstants;
import bg.sofia.uni.fmi.mjt.splitwise.server.Session;
import bg.sofia.uni.fmi.mjt.splitwise.server.repositories.splitwise.SplitWiseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * For the CommandExecutorTest and every other command executor in this package
 * I mock the final class SplitWiseRepository to isolate the responsibilities of the classes
 * following the Single Responsibility Principle and testing the validation,
 * session handling and message response format
 * but in another test package testing the actual business logic of each command is done
 * and UserRepository is being mocked and not the final class
 **/
public class CommandExecutorTest {

    @BeforeEach
    void setUp() {
        this.mockRepository = mock(SplitWiseRepository.class);
        this.mockSession = mock(Session.class);
        this.mockCommand = mock(Command.class);
    }

    @Test
    void testExecuteWithNullRepository() {
        String result = CommandExecutor.execute(null, this.mockCommand, this.mockSession);

        assertTrue(result.contains(ERROR_MESSAGE),
            "When repository is null should return unknown error message");
    }

    @Test
    void testExecuteWithNullCommand() {
        String result = CommandExecutor.execute(this.mockRepository, null, this.mockSession);

        assertTrue(result.contains(ERROR_MESSAGE),
            "When command is null should return unknown error message");
    }

    @Test
    void testExecuteWithNullSession() {
        String result = CommandExecutor.execute(this.mockRepository, this.mockCommand, null);

        assertTrue(result.contains(ERROR_MESSAGE),
            "When session is null should return unknown error message");
    }

    @Test
    void testExecuteRegistrationCommand() {
        when(this.mockCommand.command()).thenReturn(CommandConstants.REGISTRATION);
        when(this.mockCommand.arguments()).thenReturn(new String[]{"username", "password"});

        String result = CommandExecutor.execute(this.mockRepository, this.mockCommand, this.mockSession);

        assertFalse(result.contains(ERROR_MESSAGE), "Registration command should be processed");
    }

    @Test
    void testExecuteLoginCommand() {
        when(this.mockCommand.command()).thenReturn(CommandConstants.LOGIN);
        when(this.mockCommand.arguments()).thenReturn(new String[]{"username", "password"});

        String result = CommandExecutor.execute(this.mockRepository, this.mockCommand, this.mockSession);
        assertFalse(result.contains(ERROR_MESSAGE), "Login command should be processed");
    }

    @Test
    void testExecuteAddFriendCommand() {
        when(this.mockCommand.command()).thenReturn(CommandConstants.ADD_FRIEND);
        when(this.mockCommand.arguments()).thenReturn(new String[]{"friendUsername"});

        String result = CommandExecutor.execute(this.mockRepository, this.mockCommand, this.mockSession);
        assertFalse(result.contains(ERROR_MESSAGE), "Add-friend command should be processed");
    }

    @Test
    void testExecuteCreateGroupCommand() {
        when(this.mockCommand.command()).thenReturn(CommandConstants.CREATE_GROUP);
        when(this.mockCommand.arguments()).thenReturn(new String[]{"groupName", "friend1", "friend2"});

        String result = CommandExecutor.execute(this.mockRepository, this.mockCommand, this.mockSession);
        assertFalse(result.contains("Unknown error occurred"), "Create-group command should be processed");
    }

    @Test
    void testExecuteSplitCommand() {
        when(this.mockCommand.command()).thenReturn(CommandConstants.SPLIT);
        when(this.mockCommand.arguments()).thenReturn(new String[]{"20.50", "friend", "reason"});

        String result = CommandExecutor.execute(this.mockRepository, this.mockCommand, this.mockSession);
        assertFalse(result.contains(ERROR_MESSAGE), "Split command should be processed");
    }

    @Test
    void testExecuteSplitGroupCommand() {
        when(this.mockCommand.command()).thenReturn(CommandConstants.SPLIT_GROUP);
        when(this.mockCommand.arguments()).thenReturn(new String[]{"30.75", "groupName", "reason"});

        String result = CommandExecutor.execute(this.mockRepository, this.mockCommand, this.mockSession);
        assertFalse(result.contains(ERROR_MESSAGE), "Split-group command should be processed");
    }

    @Test
    void testExecuteGetStatusCommand() {
        when(this.mockCommand.command()).thenReturn(CommandConstants.GET_STATUS);
        when(this.mockCommand.arguments()).thenReturn(new String[]{});

        String result = CommandExecutor.execute(this.mockRepository, this.mockCommand, this.mockSession);
        assertFalse(result.contains(ERROR_MESSAGE), "Get-status command should be processed");
    }

    @Test
    void testExecutePayCommand() {
        when(this.mockCommand.command()).thenReturn(CommandConstants.PAY);
        when(this.mockCommand.arguments()).thenReturn(new String[]{"25.00", "friend"});

        String result = CommandExecutor.execute(this.mockRepository, this.mockCommand, this.mockSession);
        assertFalse(result.contains(ERROR_MESSAGE), "Pay command should be processed");
    }

    @Test
    void testExecuteHelpCommand() {
        when(mockCommand.command()).thenReturn(CommandConstants.HELP);
        when(mockCommand.arguments()).thenReturn(new String[]{});

        String result = CommandExecutor.execute(this.mockRepository, this.mockCommand, this.mockSession);
        assertFalse(result.contains(ERROR_MESSAGE), "Help command should return help message");
    }

    @Test
    void testExecuteCheckHistoryCommand() {
        when(this.mockCommand.command()).thenReturn(CommandConstants.CHECK_HISTORY);
        when(this.mockCommand.arguments()).thenReturn(new String[]{});

        String result = CommandExecutor.execute(this.mockRepository, this.mockCommand, this.mockSession);
        assertFalse(result.contains(ERROR_MESSAGE), "Check-history command should be processed");
    }

    @Test
    void testExecuteLogoutCommand() {
        when(this.mockCommand.command()).thenReturn(CommandConstants.LOGOUT);
        when(this.mockCommand.arguments()).thenReturn(new String[]{});

        String result = CommandExecutor.execute(this.mockRepository, this.mockCommand, this.mockSession);
        assertFalse(result.contains(ERROR_MESSAGE), "Logout command should be processed");
    }

    @Test
    void testExecuteGetFriendsCommand() {
        when(this.mockCommand.command()).thenReturn(CommandConstants.GET_FRIENDS);
        when(this.mockCommand.arguments()).thenReturn(new String[]{});

        String result = CommandExecutor.execute(this.mockRepository, this.mockCommand, this.mockSession);
        assertFalse(result.contains(ERROR_MESSAGE), "Get-friends command should be processed");
    }

    @Test
    void testExecuteGetGroupsCommand() {
        when(this.mockCommand.command()).thenReturn(CommandConstants.GET_GROUPS);
        when(this.mockCommand.arguments()).thenReturn(new String[]{});

        String result = CommandExecutor.execute(this.mockRepository, this.mockCommand, this.mockSession);
        assertFalse(result.contains(ERROR_MESSAGE), "Get-groups command should be processed");
    }

    @Test
    void testExecuteUnknownCommand() {
        when(this.mockCommand.command()).thenReturn("unknown-command");
        when(this.mockCommand.arguments()).thenReturn(new String[]{});

        String result = CommandExecutor.execute(this.mockRepository, this.mockCommand, this.mockSession);
        assertTrue(result.contains(ERROR_MESSAGE),
            "When unknown command is passed should return unknown error message");
    }

    @Test
    void testExecuteWithEmptyCommandString() {
        when(this.mockCommand.command()).thenReturn("");
        when(this.mockCommand.arguments()).thenReturn(new String[]{});

        String result = CommandExecutor.execute(this.mockRepository, this.mockCommand, this.mockSession);
        assertTrue(result.contains(ERROR_MESSAGE),
            "When empty command string is passed should return unknown error message");
    }

    @Test
    void testExecuteWithNullCommandString() {
        when(this.mockCommand.command()).thenReturn(null);
        when(this.mockCommand.arguments()).thenReturn(new String[]{});

        String result = CommandExecutor.execute(this.mockRepository, this.mockCommand, this.mockSession);
        assertTrue(result.contains(ERROR_MESSAGE),
            "When null command string is passed should return unknown error message");
    }

    @Test
    void testExecuteWithWhitespaceCommand() {
        when(this.mockCommand.command()).thenReturn("   ");
        when(this.mockCommand.arguments()).thenReturn(new String[]{});

        String result = CommandExecutor.execute(this.mockRepository, this.mockCommand, this.mockSession);
        assertTrue(result.contains(ERROR_MESSAGE),
            "When command is only whitespace should return unknown error message");
    }

    private SplitWiseRepository mockRepository;
    private Session mockSession;
    private Command mockCommand;

    private static final String ERROR_MESSAGE = "Unknown error occurred";

}