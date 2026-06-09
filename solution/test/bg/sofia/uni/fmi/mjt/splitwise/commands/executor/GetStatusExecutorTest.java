package bg.sofia.uni.fmi.mjt.splitwise.commands.executor;

import bg.sofia.uni.fmi.mjt.splitwise.exceptions.UserWithThisUsernameDoesNotExistException;
import bg.sofia.uni.fmi.mjt.splitwise.server.Session;
import bg.sofia.uni.fmi.mjt.splitwise.server.repositories.splitwise.SplitWiseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class GetStatusExecutorTest {

    @BeforeEach
    void setUp() {
        this.mockRepository = mock(SplitWiseRepository.class);
        this.mockSession = mock(Session.class);
    }

    @Test
    void testGetStatusWithNotLoggedInUser()
        throws UserWithThisUsernameDoesNotExistException {
        when(this.mockSession.isLoggedIn()).thenReturn(false);

        String result = GetStatusExecutor.execute(this.mockRepository, this.mockSession);

        assertTrue(result.contains(ERROR_MESSAGE),
            "When trying to get status of user but the user is not logged in" +
                "should return error message: " + result);

        verify(this.mockRepository, never()).getStatus(any());
    }

    @Test
    void testGetStatusWithUserDoesNotExist()
        throws UserWithThisUsernameDoesNotExistException {
        String nonExistentUsername = "nonExistent";

        when(this.mockSession.isLoggedIn()).thenReturn(true);
        when(this.mockSession.getUsername()).thenReturn(nonExistentUsername);

        doThrow(UserWithThisUsernameDoesNotExistException.class)
            .when(this.mockRepository)
            .getStatus(nonExistentUsername);

        String result = GetStatusExecutor.execute(this.mockRepository, this.mockSession);

        assertTrue(result.contains(ERROR_MESSAGE),
            "When trying to get status of user with non-existent user" +
                "should return error message: " + result);

        verify(this.mockRepository).getStatus(nonExistentUsername);
    }

    @Test
    void testGetStatusSuccessfullyWithStatusAvailable() throws Exception {
        String friendsStatus = "Friend: " + FRIEND_USERNAME_ONE + System.lineSeparator() +
            FRIEND_USERNAME_ONE + ": Owes you 20.00 [ reason 1 ] " + System.lineSeparator();
        String groupsStatus = "Group: " + GROUP_NAME + System.lineSeparator() +
            FRIEND_USERNAME_TWO + ": You owe 10.00 [ reason 2 ] " + System.lineSeparator();
        String statusMessage = friendsStatus + groupsStatus;

        when(this.mockSession.isLoggedIn()).thenReturn(true);
        when(this.mockSession.getUsername()).thenReturn(LOGGED_USERNAME);

        when(this.mockRepository.getStatus(LOGGED_USERNAME)).thenReturn(statusMessage);

        String result = GetStatusExecutor.execute(this.mockRepository, this.mockSession);

        assertEquals(result, statusMessage,
            "When trying to get status of user with valid data" +
                "should return successful message");
        verify(this.mockRepository).getStatus(LOGGED_USERNAME);
    }

    @Test
    void testGetStatusSuccessfullyWithNoStatusAvailable() throws Exception {
        String friendsStatus = "No friends debt" + System.lineSeparator();
        String groupsStatus = "No groups debt" + System.lineSeparator();
        String statusMessage = friendsStatus + groupsStatus;

        when(this.mockSession.isLoggedIn()).thenReturn(true);
        when(this.mockSession.getUsername()).thenReturn(LOGGED_USERNAME);

        when(this.mockRepository.getStatus(LOGGED_USERNAME)).thenReturn(statusMessage);

        String result = GetStatusExecutor.execute(this.mockRepository, this.mockSession);

        assertEquals(result, statusMessage,
            "When trying to get status of user with valid data" +
                "should return successful message");
        verify(this.mockRepository).getStatus(LOGGED_USERNAME);
    }

    private SplitWiseRepository mockRepository;
    private Session mockSession;

    private static final String LOGGED_USERNAME = "username";
    private static final String FRIEND_USERNAME_ONE = "friendUsername1";
    private static final String FRIEND_USERNAME_TWO = "friendUsername2";
    private static final String GROUP_NAME = "groupName";
    private static final String ERROR_MESSAGE = "ERROR";

}
