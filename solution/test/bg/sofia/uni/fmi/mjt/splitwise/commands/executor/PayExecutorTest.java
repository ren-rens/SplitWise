package bg.sofia.uni.fmi.mjt.splitwise.commands.executor;

import bg.sofia.uni.fmi.mjt.splitwise.exceptions.GroupWithThisNameDoesNotExistException;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.NoFriendWithSuchUsernameExistsException;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.UserWithThisUsernameDoesNotExistException;
import bg.sofia.uni.fmi.mjt.splitwise.server.Session;
import bg.sofia.uni.fmi.mjt.splitwise.server.repositories.splitwise.SplitWiseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class PayExecutorTest {

    @BeforeEach
    void setUp() {
        this.mockRepository = mock(SplitWiseRepository.class);
        this.mockSession = mock(Session.class);
    }

    @Test
    void testPayFriendWithNotCorrectAmountOfArguments()
        throws NoFriendWithSuchUsernameExistsException, GroupWithThisNameDoesNotExistException,
        UserWithThisUsernameDoesNotExistException {
        String[] args = {AMOUNT_STRING};
        when(this.mockSession.isLoggedIn()).thenReturn(true);

        String result = PayExecutor.execute(this.mockRepository, args, this.mockSession);

        assertTrue(result.contains(ERROR_MESSAGE),
            "When trying to pay friend with incorrect amount of arguments" +
                "should return error message: " + result);

        verify(this.mockRepository, never()).payed(any(), any(), anyDouble(), any());
    }

    @Test
    void testPayWithNotLoggedInUser()
        throws NoFriendWithSuchUsernameExistsException, GroupWithThisNameDoesNotExistException,
        UserWithThisUsernameDoesNotExistException {
        String[] args = {AMOUNT_STRING, FRIEND_USERNAME};
        when(this.mockSession.isLoggedIn()).thenReturn(false);

        String result = PayExecutor.execute(this.mockRepository, args, this.mockSession);

        assertTrue(result.contains(ERROR_MESSAGE),
            "When trying to pay but the user is not logged in" +
                "should return error message: " + result);

        verify(this.mockRepository, never()).payed(any(), any(), anyDouble(), any());
    }

    @Test
    void testPayFriendWithInvalidAmountFormat()
        throws NoFriendWithSuchUsernameExistsException, GroupWithThisNameDoesNotExistException,
        UserWithThisUsernameDoesNotExistException {
        String[] args = {"not_a_number", FRIEND_USERNAME};
        when(this.mockSession.isLoggedIn()).thenReturn(true);

        String result = PayExecutor.execute(this.mockRepository, args, this.mockSession);

        assertTrue(result.contains(ERROR_MESSAGE),
            "When trying to pay with invalid amount format" +
                "should return error message about number format: " + result);

        verify(this.mockRepository, never()).payed(any(), any(), anyDouble(), any());
    }

    @Test
    void testPayFriendWithUserDoesNotExist()
        throws UserWithThisUsernameDoesNotExistException,
        NoFriendWithSuchUsernameExistsException, GroupWithThisNameDoesNotExistException {
        String[] args = {AMOUNT_STRING, FRIEND_USERNAME};

        when(this.mockSession.isLoggedIn()).thenReturn(true);
        when(this.mockSession.getUsername()).thenReturn(LOGGED_USERNAME);

        doThrow(UserWithThisUsernameDoesNotExistException.class)
            .when(this.mockRepository)
            .payed(LOGGED_USERNAME, null, AMOUNT, FRIEND_USERNAME);

        String result = PayExecutor.execute(this.mockRepository, args, this.mockSession);

        assertTrue(result.contains(ERROR_MESSAGE),
            "When trying to pay with non-existent logged user" +
                "should return error message: " + result);

        verify(this.mockRepository).payed(LOGGED_USERNAME, null, AMOUNT, FRIEND_USERNAME);
    }

    @Test
    void testPayFriendWithFriendDoesNotExist()
        throws UserWithThisUsernameDoesNotExistException, NoFriendWithSuchUsernameExistsException,
        GroupWithThisNameDoesNotExistException {
        String[] args = {AMOUNT_STRING, NON_EXISTENT_USERNAME};

        when(this.mockSession.isLoggedIn()).thenReturn(true);
        when(this.mockSession.getUsername()).thenReturn(LOGGED_USERNAME);

        doThrow(NoFriendWithSuchUsernameExistsException.class)
            .when(this.mockRepository)
            .payed(LOGGED_USERNAME, null, AMOUNT, NON_EXISTENT_USERNAME);

        String result = PayExecutor.execute(this.mockRepository, args, this.mockSession);

        assertTrue(result.contains(ERROR_MESSAGE),
            "When trying to pay non-existent friend" +
                "should return error message: " + result);

        verify(this.mockRepository).payed(LOGGED_USERNAME, null, AMOUNT, NON_EXISTENT_USERNAME);
    }

    @Test
    void testPayGroupMemberWithGroupDoesNotExist()
        throws UserWithThisUsernameDoesNotExistException, NoFriendWithSuchUsernameExistsException,
        GroupWithThisNameDoesNotExistException {
        String[] args = {AMOUNT_STRING, GROUP_NAME, FRIEND_USERNAME};

        when(this.mockSession.isLoggedIn()).thenReturn(true);
        when(this.mockSession.getUsername()).thenReturn(LOGGED_USERNAME);

        doThrow(GroupWithThisNameDoesNotExistException.class)
            .when(this.mockRepository)
            .payed(LOGGED_USERNAME, GROUP_NAME, AMOUNT, FRIEND_USERNAME);

        String result = PayExecutor.execute(this.mockRepository, args, this.mockSession);

        assertTrue(result.contains(ERROR_MESSAGE),
            "When trying to pay in non-existent group" +
                "should return error message: " + result);

        verify(this.mockRepository).payed(LOGGED_USERNAME, GROUP_NAME, AMOUNT, FRIEND_USERNAME);
    }

    @Test
    void testPayGroupMemberWithFriendDoesNotExistInGroup()
        throws UserWithThisUsernameDoesNotExistException, NoFriendWithSuchUsernameExistsException,
        GroupWithThisNameDoesNotExistException {
        String[] args = {AMOUNT_STRING, GROUP_NAME, NON_EXISTENT_USERNAME};

        when(this.mockSession.isLoggedIn()).thenReturn(true);
        when(this.mockSession.getUsername()).thenReturn(LOGGED_USERNAME);

        doThrow(NoFriendWithSuchUsernameExistsException.class)
            .when(this.mockRepository)
            .payed(LOGGED_USERNAME, GROUP_NAME, AMOUNT, NON_EXISTENT_USERNAME);

        String result = PayExecutor.execute(this.mockRepository, args, this.mockSession);

        assertTrue(result.contains(ERROR_MESSAGE),
            "When trying to pay non-existent member in group" +
                "should return error message: " + result);

        verify(this.mockRepository).payed(LOGGED_USERNAME, GROUP_NAME, AMOUNT, NON_EXISTENT_USERNAME);
    }

    @Test
    void testPayFriendSuccessfully()
        throws UserWithThisUsernameDoesNotExistException,
        NoFriendWithSuchUsernameExistsException,
        GroupWithThisNameDoesNotExistException {
        String[] args = {AMOUNT_STRING, FRIEND_USERNAME};

        when(this.mockSession.isLoggedIn()).thenReturn(true);
        when(this.mockSession.getUsername()).thenReturn(LOGGED_USERNAME);

        String result = PayExecutor.execute(this.mockRepository, args, this.mockSession);

        assertTrue(result.contains(OK_MESSAGE),
            "When trying to pay friend with valid data" +
                "should return successful message: " + result);

        verify(this.mockRepository).payed(LOGGED_USERNAME, null, AMOUNT, FRIEND_USERNAME);
    }

    @Test
    void testPayGroupMemberSuccessfully()
        throws UserWithThisUsernameDoesNotExistException, NoFriendWithSuchUsernameExistsException,
        GroupWithThisNameDoesNotExistException {
        String[] args = {AMOUNT_STRING, GROUP_NAME, FRIEND_USERNAME};

        when(this.mockSession.isLoggedIn()).thenReturn(true);
        when(this.mockSession.getUsername()).thenReturn(LOGGED_USERNAME);

        String result = PayExecutor.execute(this.mockRepository, args, this.mockSession);

        assertTrue(result.contains(OK_MESSAGE),
            "When trying to pay group member with valid data" +
                "should return successful message: " + result);

        verify(this.mockRepository).payed(LOGGED_USERNAME, GROUP_NAME, AMOUNT, FRIEND_USERNAME);
    }

    @Test
    void testPayFriendWithZeroAmount()
        throws NoFriendWithSuchUsernameExistsException, GroupWithThisNameDoesNotExistException,
        UserWithThisUsernameDoesNotExistException {
        String[] args = {"0.0", FRIEND_USERNAME};

        when(this.mockSession.isLoggedIn()).thenReturn(true);
        when(this.mockSession.getUsername()).thenReturn(LOGGED_USERNAME);

        String result = PayExecutor.execute(this.mockRepository, args, this.mockSession);

        assertTrue(result.contains(ERROR_MESSAGE),
            "When paying with zero amount should return error message: " + result);

        verify(this.mockRepository, never()).payed(any(), any(), anyDouble(), any());
    }

    @Test
    void testPayFriendWithNegativeAmount()
        throws NoFriendWithSuchUsernameExistsException, GroupWithThisNameDoesNotExistException,
        UserWithThisUsernameDoesNotExistException {
        String[] args = {"-10.0", FRIEND_USERNAME};

        when(this.mockSession.isLoggedIn()).thenReturn(true);
        when(this.mockSession.getUsername()).thenReturn(LOGGED_USERNAME);

        String result = PayExecutor.execute(this.mockRepository, args, this.mockSession);

        assertTrue(result.contains(ERROR_MESSAGE),
            "When paying with negative amount should return error message: " + result);

        verify(this.mockRepository, never()).payed(any(), any(), anyDouble(), any());
    }

    private SplitWiseRepository mockRepository;
    private Session mockSession;

    private static final String LOGGED_USERNAME = "username";
    private static final String FRIEND_USERNAME = "friendUsername";
    private static final String NON_EXISTENT_USERNAME = "nonExistentUser";
    private static final String GROUP_NAME = "groupName";
    private static final String AMOUNT_STRING = "20.50";
    private static final double AMOUNT = 20.50;
    private static final String ERROR_MESSAGE = "ERROR";
    private static final String OK_MESSAGE = "OK";

}