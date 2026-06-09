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

public class SplitWithGroupExecutorTest {

    @BeforeEach
    void setUp() {
        this.mockRepository = mock(SplitWiseRepository.class);
        this.mockSession = mock(Session.class);
    }

    @Test
    void testSplitWithGroupWithNotCorrectAmountOfArguments()
        throws NoFriendWithSuchUsernameExistsException, GroupWithThisNameDoesNotExistException,
        UserWithThisUsernameDoesNotExistException {
        String[] args = {AMOUNT_STRING, GROUP_NAME};
        when(this.mockSession.isLoggedIn()).thenReturn(true);

        String result = SplitWithGroupExecutor.execute(this.mockRepository, args, this.mockSession);

        assertTrue(result.contains(ERROR_MESSAGE),
            "When trying to split with group with incorrect amount of arguments" +
                "should return error message: " + result);

        verify(this.mockRepository, never()).split(any(UserSplitRequest.class));
    }

    @Test
    void testSplitWithGroupWithNotLoggedInUser()
        throws NoFriendWithSuchUsernameExistsException, GroupWithThisNameDoesNotExistException,
        UserWithThisUsernameDoesNotExistException {
        String[] args = {AMOUNT_STRING, GROUP_NAME, REASON};
        when(this.mockSession.isLoggedIn()).thenReturn(false);

        String result = SplitWithGroupExecutor.execute(this.mockRepository, args, this.mockSession);

        assertTrue(result.contains(ERROR_MESSAGE),
            "When trying to split with group but the user is not logged in" +
                "should return error message: " + result);

        verify(this.mockRepository, never()).split(any(UserSplitRequest.class));
    }

    @Test
    void testSplitWithGroupWithInvalidAmountFormat()
        throws NoFriendWithSuchUsernameExistsException, GroupWithThisNameDoesNotExistException,
        UserWithThisUsernameDoesNotExistException {
        String[] args = {"not_a_number", GROUP_NAME, REASON};
        when(this.mockSession.isLoggedIn()).thenReturn(true);

        String result = SplitWithGroupExecutor.execute(this.mockRepository, args, this.mockSession);

        assertTrue(result.contains(ERROR_MESSAGE),
            "When trying to split with group with invalid amount format" +
                "should return error message about number format: " + result);

        verify(this.mockRepository, never()).split(any(UserSplitRequest.class));
    }

    @Test
    void testSplitWithGroupWithUserDoesNotExist()
        throws UserWithThisUsernameDoesNotExistException, NoFriendWithSuchUsernameExistsException,
        GroupWithThisNameDoesNotExistException {
        String[] args = {AMOUNT_STRING, GROUP_NAME, REASON};

        when(this.mockSession.isLoggedIn()).thenReturn(true);
        when(this.mockSession.getUsername()).thenReturn(LOGGED_USERNAME);

        doThrow(UserWithThisUsernameDoesNotExistException.class)
            .when(this.mockRepository)
            .split(any(UserSplitRequest.class));

        String result = SplitWithGroupExecutor.execute(this.mockRepository, args, this.mockSession);

        assertTrue(result.contains(ERROR_MESSAGE),
            "When trying to split with group with non-existent logged user" +
                "should return error message: " + result);

        verify(this.mockRepository)
            .split(any(UserSplitRequest.class));
    }

    @Test
    void testSplitWithGroupWithGroupDoesNotExist()
        throws UserWithThisUsernameDoesNotExistException, NoFriendWithSuchUsernameExistsException,
        GroupWithThisNameDoesNotExistException {
        String[] args = {AMOUNT_STRING, NON_EXISTENT_GROUP, REASON};

        when(this.mockSession.isLoggedIn()).thenReturn(true);
        when(this.mockSession.getUsername()).thenReturn(LOGGED_USERNAME);

        doThrow(GroupWithThisNameDoesNotExistException.class)
            .when(this.mockRepository)
            .split(any(UserSplitRequest.class));

        String result = SplitWithGroupExecutor.execute(this.mockRepository, args, this.mockSession);

        assertTrue(result.contains(ERROR_MESSAGE),
            "When trying to split with non-existent group" +
                "should return error message: " + result);

        verify(this.mockRepository)
            .split(any(UserSplitRequest.class));
    }

    @Test
    void testSplitWithGroupWithNotAllUsersExist()
        throws UserWithThisUsernameDoesNotExistException,
        NoFriendWithSuchUsernameExistsException,
        GroupWithThisNameDoesNotExistException {
        String[] args = {AMOUNT_STRING, GROUP_NAME, REASON};

        when(this.mockSession.isLoggedIn()).thenReturn(true);
        when(this.mockSession.getUsername()).thenReturn(LOGGED_USERNAME);

        doThrow(NoFriendWithSuchUsernameExistsException.class)
            .when(this.mockRepository)
            .split(any(UserSplitRequest.class));

        String result = SplitWithGroupExecutor.execute(this.mockRepository, args, this.mockSession);

        assertTrue(result.contains(ERROR_MESSAGE),
            "When trying to split with group where not all users exist" +
                "should return error message: " + result);

        verify(this.mockRepository)
            .split(any(UserSplitRequest.class));
    }

    @Test
    void testSplitWithGroupSuccessfully()
        throws UserWithThisUsernameDoesNotExistException,
        NoFriendWithSuchUsernameExistsException,
        GroupWithThisNameDoesNotExistException {
        String[] args = {AMOUNT_STRING, GROUP_NAME, REASON};

        when(this.mockSession.isLoggedIn()).thenReturn(true);
        when(this.mockSession.getUsername()).thenReturn(LOGGED_USERNAME);

        String result = SplitWithGroupExecutor.execute(this.mockRepository, args, this.mockSession);

        assertTrue(result.contains(OK_MESSAGE),
            "When trying to split with group with valid data" +
                "should return successful message: " + result);

        verify(this.mockRepository)
            .split(any(UserSplitRequest.class));
    }

    @Test
    void testSplitWithGroupWithZeroAmount()
        throws NoFriendWithSuchUsernameExistsException, GroupWithThisNameDoesNotExistException,
        UserWithThisUsernameDoesNotExistException {
        String[] args = {"0.0", GROUP_NAME, REASON};

        when(this.mockSession.isLoggedIn()).thenReturn(true);
        when(this.mockSession.getUsername()).thenReturn(LOGGED_USERNAME);

        String result = SplitWithGroupExecutor.execute(this.mockRepository, args, this.mockSession);

        assertTrue(result.contains(ERROR_MESSAGE),
            "When splitting with group with zero amount should return error message: " + result);

        verify(this.mockRepository, never()).split(any(UserSplitRequest.class));
    }

    @Test
    void testSplitWithGroupWithNegativeAmount()
        throws NoFriendWithSuchUsernameExistsException, GroupWithThisNameDoesNotExistException,
        UserWithThisUsernameDoesNotExistException {
        String[] args = {"-10.0", GROUP_NAME, REASON};

        when(this.mockSession.isLoggedIn()).thenReturn(true);
        when(this.mockSession.getUsername()).thenReturn(LOGGED_USERNAME);

        String result = SplitWithGroupExecutor.execute(this.mockRepository, args, this.mockSession);

        assertTrue(result.contains(ERROR_MESSAGE),
            "When splitting with group with negative amount should return error message: " + result);

        verify(this.mockRepository, never()).split(any(UserSplitRequest.class));
    }

    @Test
    void testSplitWithGroupWithReasonContainingSpaces()
        throws UserWithThisUsernameDoesNotExistException, NoFriendWithSuchUsernameExistsException,
        GroupWithThisNameDoesNotExistException {
        String reasonWithSpaces = "Dinner at fancy restaurant";
        String[] args = {AMOUNT_STRING, GROUP_NAME, reasonWithSpaces};

        when(this.mockSession.isLoggedIn()).thenReturn(true);
        when(this.mockSession.getUsername()).thenReturn(LOGGED_USERNAME);

        String result = SplitWithGroupExecutor.execute(this.mockRepository, args, this.mockSession);

        assertTrue(result.contains(OK_MESSAGE),
            "When splitting with group with reason containing spaces should be successful: " + result);

        verify(this.mockRepository)
            .split(any(UserSplitRequest.class));
    }

    private SplitWiseRepository mockRepository;
    private Session mockSession;

    private static final String LOGGED_USERNAME = "username";
    private static final String GROUP_NAME = "groupName";
    private static final String NON_EXISTENT_GROUP = "nonExistentGroup";
    private static final String AMOUNT_STRING = "75.50";
    private static final String REASON = "vacation";
    private static final String ERROR_MESSAGE = "ERROR";
    private static final String OK_MESSAGE = "OK";

}