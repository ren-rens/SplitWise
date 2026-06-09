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

public class GetFriendsExecutorTest {

    @BeforeEach
    void setUp() {
        this.mockRepository = mock(SplitWiseRepository.class);
        this.mockSession = mock(Session.class);
    }

    @Test
    void testGetFriendsWithNotLoggedInUser()
        throws UserWithThisUsernameDoesNotExistException {
        when(this.mockSession.isLoggedIn()).thenReturn(false);

        String result = GetFriendsExecutor.execute(this.mockRepository, this.mockSession);
        assertTrue(result.contains(ERROR_MESSAGE),
            "When trying to get friends but the user is not logged in" +
                "should return error message: " + result);

        verify(this.mockRepository, never()).getFriendsOfUser(any());
    }

    @Test
    void testGetFriendsWithUserDoesNotExist()
        throws UserWithThisUsernameDoesNotExistException {
        String nonExistentUsername = "nonExistent";

        when(this.mockSession.isLoggedIn()).thenReturn(true);
        when(this.mockSession.getUsername()).thenReturn(nonExistentUsername);

        doThrow(UserWithThisUsernameDoesNotExistException.class)
            .when(this.mockRepository)
            .getFriendsOfUser(nonExistentUsername);

        String result = GetFriendsExecutor.execute(this.mockRepository, this.mockSession);
        assertTrue(result.contains(ERROR_MESSAGE),
            "When trying to get friends with non-existent user" +
                "should return error message: " + result);

        verify(this.mockRepository).getFriendsOfUser(nonExistentUsername);
    }

    @Test
    void testGetFriendsSuccessfully() throws Exception {
        String friendsMessage = "Friend: " + FRIEND_USERNAME_ONE;

        when(this.mockSession.isLoggedIn()).thenReturn(true);
        when(this.mockSession.getUsername()).thenReturn(LOGGED_USERNAME);

        when(this.mockRepository.getFriendsOfUser(LOGGED_USERNAME)).thenReturn(friendsMessage);

        String result = GetFriendsExecutor.execute(this.mockRepository, this.mockSession);

        assertEquals(friendsMessage, result,
            "When trying to get friends with valid data" +
                "should return successful message");
        verify(this.mockRepository).getFriendsOfUser(LOGGED_USERNAME);
    }

    private SplitWiseRepository mockRepository;
    private Session mockSession;

    private static final String LOGGED_USERNAME = "username";
    private static final String FRIEND_USERNAME_ONE = "friendUsername1";
    private static final String ERROR_MESSAGE = "ERROR";

}
