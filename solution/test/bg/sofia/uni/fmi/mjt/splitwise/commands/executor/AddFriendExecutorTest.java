package bg.sofia.uni.fmi.mjt.splitwise.commands.executor;

import bg.sofia.uni.fmi.mjt.splitwise.exceptions.UserAlreadyHasFriendWithSuchUsernameException;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.UserCannotAddSelfAsFriend;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.UserDataMustContainAtLeastOneNonBlankSymbolException;
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

public class AddFriendExecutorTest {

    @BeforeEach
    void setUp() {
        this.mockRepository = mock(SplitWiseRepository.class);
        this.mockSession = mock(Session.class);
    }

    @Test
    void testAddFriendWithNoArguments()
        throws UserWithThisUsernameDoesNotExistException, UserAlreadyHasFriendWithSuchUsernameException,
        UserCannotAddSelfAsFriend, UserDataMustContainAtLeastOneNonBlankSymbolException {
        String[] args = {};

        String result = AddFriendExecutor.execute(this.mockRepository, args, this.mockSession);

        assertTrue(result.contains(ERROR_MESSAGE),
            "When testing add friend with incorrect amount of arguments" +
                "should return error message: " + result);
        verify(this.mockRepository, never()).addFriend(any(), any());
    }

    @Test
    void testAddFriendWhenUserIsNotLoggedIn()
        throws UserWithThisUsernameDoesNotExistException, UserAlreadyHasFriendWithSuchUsernameException,
        UserCannotAddSelfAsFriend, UserDataMustContainAtLeastOneNonBlankSymbolException {
        String[] arguments = {FRIEND_USERNAME};
        when(this.mockSession.isLoggedIn()).thenReturn(false);

        String result = AddFriendExecutor.execute(this.mockRepository, arguments, this.mockSession);

        assertTrue(result.contains(ERROR_MESSAGE),
            "When testing add friend with user that is not logged in" +
                "should return error message: " + result);
        verify(this.mockRepository, never()).addFriend(any(), any());
    }

    @Test
    void testAddFriendSuccessful() throws Exception {
        String[] arguments = {FRIEND_USERNAME};
        when(this.mockSession.isLoggedIn()).thenReturn(true);
        when(this.mockSession.getUsername()).thenReturn(LOGGED_USERNAME);

        String result = AddFriendExecutor.execute(this.mockRepository, arguments, this.mockSession);

        assertTrue(result.contains(OK_MESSAGE),
            "When testing add friend with valid data" +
                "should return successful message: " + result);
        verify(this.mockRepository).addFriend(LOGGED_USERNAME, FRIEND_USERNAME);
    }

    @Test
    void testAddFriendWhenFriendDoesNotExist() throws Exception {
        String[] arguments = {"nonExistentFriend"};
        when(this.mockSession.isLoggedIn()).thenReturn(true);
        when(this.mockSession.getUsername()).thenReturn(LOGGED_USERNAME);

        doThrow(UserWithThisUsernameDoesNotExistException.class)
            .when(this.mockRepository).addFriend(LOGGED_USERNAME, "nonExistentFriend");

        String result = AddFriendExecutor.execute(this.mockRepository, arguments, this.mockSession);

        assertTrue(result.contains(ERROR_MESSAGE),
            "When testing add friend with non-existent friend" +
                "should return error message: " + result);
        verify(this.mockRepository).addFriend(LOGGED_USERNAME, "nonExistentFriend");
    }

    @Test
    void testAddFriendWhenAlreadyFriends() throws Exception {
        String[] arguments = {FRIEND_USERNAME};
        when(this.mockSession.isLoggedIn()).thenReturn(true);
        when(this.mockSession.getUsername()).thenReturn(LOGGED_USERNAME);

        doThrow(UserAlreadyHasFriendWithSuchUsernameException.class)
            .when(this.mockRepository).addFriend(LOGGED_USERNAME, FRIEND_USERNAME);

        String result = AddFriendExecutor.execute(this.mockRepository, arguments, this.mockSession);

        assertTrue(result.contains(ERROR_MESSAGE),
            "When testing add friend with a user that is already friend of the user" +
                "should return error message: " + result);
        verify(this.mockRepository).addFriend(LOGGED_USERNAME, FRIEND_USERNAME);
    }

    @Test
    void testAddFriendAddingSelfAsFriend() throws Exception {
        String[] arguments = {LOGGED_USERNAME};
        when(this.mockSession.isLoggedIn()).thenReturn(true);
        when(this.mockSession.getUsername()).thenReturn(LOGGED_USERNAME);

        doThrow(UserCannotAddSelfAsFriend.class)
            .when(this.mockRepository).addFriend(LOGGED_USERNAME, LOGGED_USERNAME);


        String result = AddFriendExecutor.execute(this.mockRepository, arguments, this.mockSession);
        assertTrue(result.contains(ERROR_MESSAGE),
            "When testing add friend with the username of the logged user" +
                "should return error message: " + result);
        verify(this.mockRepository).addFriend(LOGGED_USERNAME, LOGGED_USERNAME);
    }

    @Test
    void testAddFriendAddingNullAsFriend() throws Exception {
        String[] arguments = {null};
        when(this.mockSession.isLoggedIn()).thenReturn(true);
        when(this.mockSession.getUsername()).thenReturn(LOGGED_USERNAME);

        doThrow(UserDataMustContainAtLeastOneNonBlankSymbolException.class)
            .when(this.mockRepository).addFriend(LOGGED_USERNAME, null);


        String result = AddFriendExecutor.execute(this.mockRepository, arguments, this.mockSession);
        assertTrue(result.contains(ERROR_MESSAGE),
            "When testing add friend with the username of friend null" +
                "should return error message: " + result);
        verify(this.mockRepository).addFriend(LOGGED_USERNAME, null);
    }

    @Test
    void testAddFriendAddingBlankUsernameAsFriend() throws Exception {
        String friend = "    ";
        String[] arguments = {friend};
        when(this.mockSession.isLoggedIn()).thenReturn(true);
        when(this.mockSession.getUsername()).thenReturn(LOGGED_USERNAME);

        doThrow(UserDataMustContainAtLeastOneNonBlankSymbolException.class)
            .when(this.mockRepository).addFriend(LOGGED_USERNAME, friend);


        String result = AddFriendExecutor.execute(this.mockRepository, arguments, this.mockSession);
        assertTrue(result.contains(ERROR_MESSAGE),
            "When testing add friend with the username of friend BLANK" +
                "should return error message: " + result);
        verify(this.mockRepository).addFriend(LOGGED_USERNAME, friend);
    }

    @Test
    void testAddFriendAddingWithNullUsername() throws Exception {
        String[] arguments = {FRIEND_USERNAME};
        when(this.mockSession.isLoggedIn()).thenReturn(true);
        when(this.mockSession.getUsername()).thenReturn(null);

        doThrow(UserCannotAddSelfAsFriend.class)
            .when(this.mockRepository).addFriend(null, FRIEND_USERNAME);


        String result = AddFriendExecutor.execute(this.mockRepository, arguments, this.mockSession);
        assertTrue(result.contains(ERROR_MESSAGE),
            "When testing add friend with the username of user null" +
                "should return error message: " + result);
        verify(this.mockRepository).addFriend(null, FRIEND_USERNAME);
    }

    @Test
    void testAddFriendAddingBlankUsername() throws Exception {
        String username = "    ";
        String[] arguments = {FRIEND_USERNAME};
        when(this.mockSession.isLoggedIn()).thenReturn(true);
        when(this.mockSession.getUsername()).thenReturn(username);

        doThrow(UserDataMustContainAtLeastOneNonBlankSymbolException.class)
            .when(this.mockRepository).addFriend(username, FRIEND_USERNAME);


        String result = AddFriendExecutor.execute(this.mockRepository, arguments, this.mockSession);
        assertTrue(result.contains(ERROR_MESSAGE),
            "When testing add friend from a user with blank username" +
                "should return error message: " + result);
        verify(this.mockRepository).addFriend(username, FRIEND_USERNAME);
    }

    private SplitWiseRepository mockRepository;
    private Session mockSession;

    private static final String LOGGED_USERNAME = "username";
    private static final String FRIEND_USERNAME = "friendUsername";

    private static final String ERROR_MESSAGE = "ERROR";
    private static final String OK_MESSAGE = "OK";

}