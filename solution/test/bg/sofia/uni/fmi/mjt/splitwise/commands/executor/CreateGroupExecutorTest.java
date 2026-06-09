package bg.sofia.uni.fmi.mjt.splitwise.commands.executor;

import bg.sofia.uni.fmi.mjt.splitwise.exceptions.GroupNamesMustContainAtLeastOneNonBlankSymbolException;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.GroupWithThisNameAlreadyExistsException;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.NotEnoughUsersToCreateAGroupException;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.UserWithThisUsernameDoesNotExistException;
import bg.sofia.uni.fmi.mjt.splitwise.server.Session;
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

public class CreateGroupExecutorTest {

    @BeforeEach
    void setUp() {
        this.mockRepository = mock(SplitWiseRepository.class);
        this.mockSession = mock(Session.class);
    }

    @Test
    void testCreateGroupWithNotCorrectAmountOfArguments()
        throws UserWithThisUsernameDoesNotExistException, NotEnoughUsersToCreateAGroupException,
        GroupWithThisNameAlreadyExistsException, GroupNamesMustContainAtLeastOneNonBlankSymbolException {
        String[] args = {GROUP_NAME};
        when(this.mockSession.isLoggedIn()).thenReturn(true);

        String result = CreateGroupExecutor.execute(this.mockRepository, args, this.mockSession);
        assertTrue(result.contains(ERROR_MESSAGE),
            "When trying to create group with incorrect amount of arguments" +
                "should return error message: " + result);

        verify(this.mockRepository, never()).createGroup(any(), any(), any());
    }

    @Test
    void testCreateGroupWithNotLoggedInUser()
        throws UserWithThisUsernameDoesNotExistException, NotEnoughUsersToCreateAGroupException,
        GroupWithThisNameAlreadyExistsException, GroupNamesMustContainAtLeastOneNonBlankSymbolException {
        String[] args = {GROUP_NAME, FRIEND_USERNAME_ONE, FRIEND_USERNAME_TWO};
        when(this.mockSession.isLoggedIn()).thenReturn(false);

        String result = CreateGroupExecutor.execute(this.mockRepository, args, this.mockSession);
        assertTrue(result.contains(ERROR_MESSAGE),
            "When trying to create group but the user is not logged in" +
                "should return error message: " + result);

        verify(this.mockRepository, never()).createGroup(any(), any(), any());
    }

    @Test
    void testCreateGroupWithNullGroupName() throws Exception {
        String[] args = {null, FRIEND_USERNAME_ONE, FRIEND_USERNAME_TWO};
        String[] friends = {FRIEND_USERNAME_ONE, FRIEND_USERNAME_TWO};

        when(this.mockSession.isLoggedIn()).thenReturn(true);
        when(this.mockSession.getUsername()).thenReturn(LOGGED_USERNAME);

        doThrow(GroupNamesMustContainAtLeastOneNonBlankSymbolException.class)
            .when(this.mockRepository).createGroup(LOGGED_USERNAME, null, friends);

        String result = CreateGroupExecutor.execute(this.mockRepository, args, this.mockSession);

        assertTrue(result.contains(ERROR_MESSAGE),
            "When trying to create group with group name NULL" +
                "should return error message");
        verify(this.mockRepository).createGroup(LOGGED_USERNAME, null, friends);
    }

    @Test
    void testCreateGroupWithBlankGroupName() throws Exception {
        String blankGroupName = "    ";
        String[] args = {blankGroupName, FRIEND_USERNAME_ONE, FRIEND_USERNAME_TWO};
        String[] friends = {FRIEND_USERNAME_ONE, FRIEND_USERNAME_TWO};

        when(this.mockSession.isLoggedIn()).thenReturn(true);
        when(this.mockSession.getUsername()).thenReturn(LOGGED_USERNAME);

        doThrow(GroupNamesMustContainAtLeastOneNonBlankSymbolException.class)
            .when(this.mockRepository).createGroup(LOGGED_USERNAME, blankGroupName, friends);

        String result = CreateGroupExecutor.execute(this.mockRepository, args, this.mockSession);

        assertTrue(result.contains(ERROR_MESSAGE),
            "When trying to create group with group name BLANK" +
                "should return error message");
        verify(this.mockRepository).createGroup(LOGGED_USERNAME, blankGroupName, friends);
    }

    @Test
    void testCreateGroupWithUserDoesNotExist()
        throws UserWithThisUsernameDoesNotExistException, NotEnoughUsersToCreateAGroupException,
        GroupWithThisNameAlreadyExistsException, GroupNamesMustContainAtLeastOneNonBlankSymbolException {
        String nonExistentUsername = "nonExistent";
        String[] args = {GROUP_NAME, nonExistentUsername, FRIEND_USERNAME_TWO};

        when(this.mockSession.isLoggedIn()).thenReturn(true);
        when(this.mockSession.getUsername()).thenReturn(nonExistentUsername);

        doThrow(UserWithThisUsernameDoesNotExistException.class)
            .when(this.mockRepository)
            .createGroup(nonExistentUsername, GROUP_NAME, new String[] {nonExistentUsername, FRIEND_USERNAME_TWO});

        String result = CreateGroupExecutor.execute(this.mockRepository, args, this.mockSession);

        assertTrue(result.contains(ERROR_MESSAGE),
            "When trying to create group with non-existent user" +
                "should return error message: " + result);

        verify(this.mockRepository).createGroup(nonExistentUsername, GROUP_NAME,
            new String[] {nonExistentUsername, FRIEND_USERNAME_TWO});
    }

    @Test
    void testCreateGroupWithNotEnoughFriends()
        throws UserWithThisUsernameDoesNotExistException, NotEnoughUsersToCreateAGroupException,
        GroupWithThisNameAlreadyExistsException, GroupNamesMustContainAtLeastOneNonBlankSymbolException {
        String[] args = {GROUP_NAME, FRIEND_USERNAME_ONE};

        when(this.mockSession.isLoggedIn()).thenReturn(true);
        when(this.mockSession.getUsername()).thenReturn(LOGGED_USERNAME);
        doThrow(NotEnoughUsersToCreateAGroupException.class)
            .when(this.mockRepository)
            .createGroup(LOGGED_USERNAME, GROUP_NAME, new String[]{FRIEND_USERNAME_ONE});

        String result = CreateGroupExecutor.execute(this.mockRepository, args, this.mockSession);

        assertTrue(result.contains(ERROR_MESSAGE),
            "When trying to create group with not enough friends to add" +
                "should return error message: " + result);

        verify(this.mockRepository)
            .createGroup(LOGGED_USERNAME, GROUP_NAME, new String[] {FRIEND_USERNAME_ONE});
    }

    @Test
    void testCreateGroupWithAlreadyExistingGroup()
        throws UserWithThisUsernameDoesNotExistException, NotEnoughUsersToCreateAGroupException,
        GroupWithThisNameAlreadyExistsException, GroupNamesMustContainAtLeastOneNonBlankSymbolException {
        String[] args = {GROUP_NAME, FRIEND_USERNAME_ONE, FRIEND_USERNAME_TWO};
        String[] friends = {FRIEND_USERNAME_ONE, FRIEND_USERNAME_TWO};

        when(this.mockSession.isLoggedIn()).thenReturn(true);
        when(this.mockSession.getUsername()).thenReturn(LOGGED_USERNAME);

        doThrow(GroupWithThisNameAlreadyExistsException.class)
            .when(this.mockRepository).createGroup(LOGGED_USERNAME, GROUP_NAME, friends);

        String result = CreateGroupExecutor.execute(this.mockRepository, args, this.mockSession);

        assertTrue(result.contains(ERROR_MESSAGE),
            "When testing create group but a group with this groupName exist in the user repository" +
                "should return error message");

        verify(this.mockRepository).createGroup(LOGGED_USERNAME, GROUP_NAME, friends);
    }

    @Test
    void testCreateGroupSuccessfully() throws Exception {
        String[] args = {GROUP_NAME, FRIEND_USERNAME_ONE, FRIEND_USERNAME_TWO};
        String[] friends = {FRIEND_USERNAME_ONE, FRIEND_USERNAME_TWO};

        when(this.mockSession.isLoggedIn()).thenReturn(true);
        when(this.mockSession.getUsername()).thenReturn(LOGGED_USERNAME);

        String result = CreateGroupExecutor.execute(this.mockRepository, args, this.mockSession);

        assertTrue(result.contains(OK_MESSAGE),
            "When trying to create group with valid data" +
                "should return successful message");
        verify(this.mockRepository).createGroup(LOGGED_USERNAME, GROUP_NAME, friends);
    }

    private SplitWiseRepository mockRepository;
    private Session mockSession;

    private static final String LOGGED_USERNAME = "username";
    private static final String FRIEND_USERNAME_ONE = "friendUsername1";
    private static final String FRIEND_USERNAME_TWO = "friendUsername2";
    private static final String GROUP_NAME = "groupName";
    private static final String ERROR_MESSAGE = "ERROR";
    private static final String OK_MESSAGE = "OK";

}
