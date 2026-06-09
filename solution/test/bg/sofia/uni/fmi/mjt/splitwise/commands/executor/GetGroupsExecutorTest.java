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

public class GetGroupsExecutorTest {

    @BeforeEach
    void setUp() {
        this.mockRepository = mock(SplitWiseRepository.class);
        this.mockSession = mock(Session.class);
    }

    @Test
    void testGetGroupsWithNotLoggedInUser()
        throws UserWithThisUsernameDoesNotExistException {
        when(this.mockSession.isLoggedIn()).thenReturn(false);

        String result = GetGroupsExecutor.execute(this.mockRepository, this.mockSession);

        assertTrue(result.contains(ERROR_MESSAGE),
            "When trying to get groups but the user is not logged in" +
                "should return error message: " + result);

        verify(this.mockRepository, never()).getGroupOfUser(any());
    }

    @Test
    void testGetGroupsWithUserDoesNotExist()
        throws UserWithThisUsernameDoesNotExistException {
        String nonExistentUsername = "nonExistent";

        when(this.mockSession.isLoggedIn()).thenReturn(true);
        when(this.mockSession.getUsername()).thenReturn(nonExistentUsername);

        doThrow(UserWithThisUsernameDoesNotExistException.class)
            .when(this.mockRepository)
            .getGroupOfUser(nonExistentUsername);

        String result = GetGroupsExecutor.execute(this.mockRepository, this.mockSession);

        assertTrue(result.contains(ERROR_MESSAGE),
            "When trying to get groups with non-existent user" +
                "should return error message: " + result);

        verify(this.mockRepository).getGroupOfUser(nonExistentUsername);
    }

    @Test
    void testGetGroupsSuccessfully() throws Exception {
        String groupsMessage = "Group: " + GROUP_NAME + System.lineSeparator()
            + "Friends in group: " + System.lineSeparator() + "Friend: " + FRIEND_USERNAME_ONE;

        when(this.mockSession.isLoggedIn()).thenReturn(true);
        when(this.mockSession.getUsername()).thenReturn(LOGGED_USERNAME);

        when(this.mockRepository.getGroupOfUser(LOGGED_USERNAME)).thenReturn(groupsMessage);

        String result = GetGroupsExecutor.execute(this.mockRepository, this.mockSession);

        assertEquals(result, groupsMessage,
            "When trying to get groups with valid data" +
                "should return successful message");
        verify(this.mockRepository).getGroupOfUser(LOGGED_USERNAME);
    }

    private SplitWiseRepository mockRepository;
    private Session mockSession;

    private static final String LOGGED_USERNAME = "username";
    private static final String FRIEND_USERNAME_ONE = "friendUsername1";
    private static final String GROUP_NAME = "groupName";
    private static final String ERROR_MESSAGE = "ERROR";

}
