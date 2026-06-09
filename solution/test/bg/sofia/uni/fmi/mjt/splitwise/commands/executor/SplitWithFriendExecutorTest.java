package bg.sofia.uni.fmi.mjt.splitwise.commands.executor;

import bg.sofia.uni.fmi.mjt.splitwise.exceptions.GroupWithThisNameDoesNotExistException;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.NoFriendWithSuchUsernameExistsException;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.UserWithThisUsernameDoesNotExistException;
import bg.sofia.uni.fmi.mjt.splitwise.server.Session;
import bg.sofia.uni.fmi.mjt.splitwise.server.model.requests.UserSplitRequest;
import bg.sofia.uni.fmi.mjt.splitwise.server.repositories.splitwise.SplitWiseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class SplitWithFriendExecutorTest {

    @BeforeEach
    void setUp() {
        this.mockRepository = mock(SplitWiseRepository.class);
        this.mockSession = mock(Session.class);
    }

    @Test
    void testSplitWithNotCorrectAmountOfArguments()
        throws NoFriendWithSuchUsernameExistsException, GroupWithThisNameDoesNotExistException,
        UserWithThisUsernameDoesNotExistException {
        String[] args = {AMOUNT_STRING, FRIEND_USERNAME};
        when(this.mockSession.isLoggedIn()).thenReturn(true);

        String result = SplitWithFriendExecutor.execute(this.mockRepository, args, this.mockSession);

        assertTrue(result.contains(ERROR_MESSAGE),
            "When trying to split with incorrect amount of arguments" +
                "should return error message: " + result);

        verify(this.mockRepository, never()).split(any(UserSplitRequest.class));
    }

    @Test
    void testSplitWithNotLoggedInUser()
        throws NoFriendWithSuchUsernameExistsException, GroupWithThisNameDoesNotExistException,
        UserWithThisUsernameDoesNotExistException {
        String[] args = {AMOUNT_STRING, FRIEND_USERNAME, REASON};
        when(this.mockSession.isLoggedIn()).thenReturn(false);

        String result = SplitWithFriendExecutor.execute(this.mockRepository, args, this.mockSession);

        assertTrue(result.contains(ERROR_MESSAGE),
            "When trying to split but the user is not logged in" +
                "should return error message: " + result);

        verify(this.mockRepository, never()).split(any(UserSplitRequest.class));
    }

    @Test
    void testSplitWithInvalidAmountFormat()
        throws NoFriendWithSuchUsernameExistsException, GroupWithThisNameDoesNotExistException,
        UserWithThisUsernameDoesNotExistException {
        String[] args = {"not_a_number", FRIEND_USERNAME, REASON};
        when(this.mockSession.isLoggedIn()).thenReturn(true);

        String result = SplitWithFriendExecutor.execute(this.mockRepository, args, this.mockSession);

        assertTrue(result.contains(ERROR_MESSAGE),
            "When trying to split with invalid amount format" +
                "should return error message about number format: " + result);

        verify(this.mockRepository, never()).split(any(UserSplitRequest.class));
    }

    @Test
    void testSplitWithUserDoesNotExist()
        throws UserWithThisUsernameDoesNotExistException, NoFriendWithSuchUsernameExistsException,
        GroupWithThisNameDoesNotExistException {
        String[] args = {AMOUNT_STRING, FRIEND_USERNAME, REASON};

        when(this.mockSession.isLoggedIn()).thenReturn(true);
        when(this.mockSession.getUsername()).thenReturn(LOGGED_USERNAME);

        doThrow(UserWithThisUsernameDoesNotExistException.class)
            .when(this.mockRepository)
            .split(any(UserSplitRequest.class));

        String result = SplitWithFriendExecutor.execute(this.mockRepository, args, this.mockSession);

        assertTrue(result.contains(ERROR_MESSAGE),
            "When trying to split with non-existent logged user" +
                "should return error message: " + result);

        verify(this.mockRepository).split(any(UserSplitRequest.class));
    }

    @Test
    void testSplitWithFriendDoesNotExist()
        throws UserWithThisUsernameDoesNotExistException, NoFriendWithSuchUsernameExistsException,
        GroupWithThisNameDoesNotExistException {
        String[] args = {AMOUNT_STRING, NON_EXISTENT_USERNAME, REASON};

        when(this.mockSession.isLoggedIn()).thenReturn(true);
        when(this.mockSession.getUsername()).thenReturn(LOGGED_USERNAME);

        doThrow(NoFriendWithSuchUsernameExistsException.class)
            .when(this.mockRepository)
            .split(any(UserSplitRequest.class));

        String result = SplitWithFriendExecutor.execute(this.mockRepository, args, this.mockSession);

        assertTrue(result.contains(ERROR_MESSAGE),
            "When trying to split with non-existent friend" +
                "should return error message: " + result);

        verify(this.mockRepository).split(any(UserSplitRequest.class));
    }

    @Test
    void testSplitSuccessfully()
        throws UserWithThisUsernameDoesNotExistException, NoFriendWithSuchUsernameExistsException,
        GroupWithThisNameDoesNotExistException {
        String[] args = {AMOUNT_STRING, FRIEND_USERNAME, REASON};

        when(this.mockSession.isLoggedIn()).thenReturn(true);
        when(this.mockSession.getUsername()).thenReturn(LOGGED_USERNAME);

        String result = SplitWithFriendExecutor.execute(this.mockRepository, args, this.mockSession);

        assertTrue(result.contains(OK_MESSAGE),
            "When trying to split with valid data" +
                "should return successful message: " + result);

        verify(this.mockRepository).split(any(UserSplitRequest.class));
    }

    @Test
    void testSplitWithZeroAmount() {
        String[] args = {"0.0", FRIEND_USERNAME, REASON};

        when(this.mockSession.isLoggedIn()).thenReturn(true);
        when(this.mockSession.getUsername()).thenReturn(LOGGED_USERNAME);

        String result = SplitWithFriendExecutor.execute(this.mockRepository, args, this.mockSession);

        assertTrue(result.contains(ERROR_MESSAGE),
            "When splitting with zero amount should return error message: " + result);
    }

    @Test
    void testSplitWithNegativeAmount() {
        String[] args = {"-10.0", FRIEND_USERNAME, REASON};

        when(this.mockSession.isLoggedIn()).thenReturn(true);
        when(this.mockSession.getUsername()).thenReturn(LOGGED_USERNAME);

        String result = SplitWithFriendExecutor.execute(this.mockRepository, args, this.mockSession);

        assertTrue(result.contains(ERROR_MESSAGE),
            "When splitting with negative amount should return error message: " + result);
    }

    @Test
    void testSplitWithReasonContainingSpaces()
        throws UserWithThisUsernameDoesNotExistException, NoFriendWithSuchUsernameExistsException,
        GroupWithThisNameDoesNotExistException {
        String reasonWithSpaces = "reason for payment";
        String[] args = {AMOUNT_STRING, FRIEND_USERNAME, reasonWithSpaces};

        when(this.mockSession.isLoggedIn()).thenReturn(true);
        when(this.mockSession.getUsername()).thenReturn(LOGGED_USERNAME);

        String result = SplitWithFriendExecutor.execute(this.mockRepository, args, this.mockSession);

        assertTrue(result.contains(OK_MESSAGE),
            "When splitting with reason containing spaces should be successful: " + result);

        verify(this.mockRepository).split(any(UserSplitRequest.class));
    }

    @Test
    void testSplitWithGroupException()
        throws UserWithThisUsernameDoesNotExistException, NoFriendWithSuchUsernameExistsException,
        GroupWithThisNameDoesNotExistException {
        String[] args = {AMOUNT_STRING, FRIEND_USERNAME, REASON};

        when(this.mockSession.isLoggedIn()).thenReturn(true);
        when(this.mockSession.getUsername()).thenReturn(LOGGED_USERNAME);

        doThrow(GroupWithThisNameDoesNotExistException.class)
            .when(this.mockRepository)
            .split(any(UserSplitRequest.class));

        String result = SplitWithFriendExecutor.execute(this.mockRepository, args, this.mockSession);

        assertTrue(result.contains("The this message should not appear"),
            "When GroupWithThisNameDoesNotExistException is thrown" +
                "should return specific message: " + result);

        verify(this.mockRepository).split(any(UserSplitRequest.class));
    }

    private SplitWiseRepository mockRepository;
    private Session mockSession;

    private static final String LOGGED_USERNAME = "username";
    private static final String FRIEND_USERNAME = "friendUsername";
    private static final String NON_EXISTENT_USERNAME = "nonExistentUser";
    private static final String AMOUNT_STRING = "50.25";
    private static final String REASON = "dinner";
    private static final String ERROR_MESSAGE = "ERROR";
    private static final String OK_MESSAGE = "OK";

}