package bg.sofia.uni.fmi.mjt.splitwise.server.repositories.splitwise.processors;

import bg.sofia.uni.fmi.mjt.splitwise.exceptions.UserAlreadyHasFriendWithSuchUsernameException;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.UserCannotAddSelfAsFriend;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.UserDataMustContainAtLeastOneNonBlankSymbolException;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.UserWithThisUsernameDoesNotExistException;
import bg.sofia.uni.fmi.mjt.splitwise.server.model.user.User;
import bg.sofia.uni.fmi.mjt.splitwise.server.repositories.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class FriendAddingProcessorTest {

    @BeforeEach
    void setUp() {
        this.mockUserRepository = mock(UserRepository.class);
        this.loggedUser = new User(LOGGED_USERNAME, "password");
        this.friendUser = new User(FRIEND_USERNAME, "password");
    }

    @Test
    void testAddFriendSuccessfully() throws Exception {
        when(this.mockUserRepository.findUserByUsername(LOGGED_USERNAME)).thenReturn(this.loggedUser);
        when(this.mockUserRepository.findUserByUsername(FRIEND_USERNAME)).thenReturn(this.friendUser);
        when(this.mockUserRepository.doesUserExist(LOGGED_USERNAME)).thenReturn(true);
        when(this.mockUserRepository.doesUserExist(FRIEND_USERNAME)).thenReturn(true);

        FriendAddingProcessor.addFriend(this.mockUserRepository, LOGGED_USERNAME, FRIEND_USERNAME);

        assertTrue(this.loggedUser.getFriends().containsKey(FRIEND_USERNAME),
            "Logged user should have friend in friends list");
        assertTrue(this.friendUser.getFriends().containsKey(LOGGED_USERNAME),
            "Friend should have logged user in friends list");

        verify(this.mockUserRepository, times(2)).updateUsers(any(User.class));
        verify(this.mockUserRepository).updateUsers(this.loggedUser);
        verify(this.mockUserRepository).updateUsers(this.friendUser);
    }

    @Test
    void testAddFriendWhenLoggedUserDoesNotExist() {
        when(this.mockUserRepository.findUserByUsername(FRIEND_USERNAME)).thenReturn(this.friendUser);
        when(this.mockUserRepository.doesUserExist(FRIEND_USERNAME)).thenReturn(true);

        when(this.mockUserRepository.doesUserExist(NON_EXISTENT_USERNAME)).thenReturn(false);
        when(this.mockUserRepository.findUserByUsername(NON_EXISTENT_USERNAME))
            .thenReturn(null);

        assertThrows(UserWithThisUsernameDoesNotExistException.class, () ->
                FriendAddingProcessor.addFriend(this.mockUserRepository, NON_EXISTENT_USERNAME, FRIEND_USERNAME),
            "Should throw exception when logged user does not exist"
        );

        verify(this.mockUserRepository, never()).updateUsers(any(User.class));
    }

    @Test
    void testAddFriendWhenFriendDoesNotExist() {
        when(this.mockUserRepository.findUserByUsername(LOGGED_USERNAME)).thenReturn(this.loggedUser);
        when(this.mockUserRepository.doesUserExist(NON_EXISTENT_USERNAME)).thenReturn(false);

        assertThrows(UserWithThisUsernameDoesNotExistException.class, () ->
                FriendAddingProcessor.addFriend(this.mockUserRepository, LOGGED_USERNAME, NON_EXISTENT_USERNAME),
            "Should throw exception when friend does not exist"
        );

        verify(this.mockUserRepository, never()).updateUsers(any(User.class));
    }

    @Test
    void testAddFriendWhenAlreadyFriends() throws UserAlreadyHasFriendWithSuchUsernameException {
        this.loggedUser.addFriend(FRIEND_USERNAME);

        when(this.mockUserRepository.findUserByUsername(LOGGED_USERNAME)).thenReturn(this.loggedUser);
        when(this.mockUserRepository.findUserByUsername(FRIEND_USERNAME)).thenReturn(this.friendUser);
        when(this.mockUserRepository.doesUserExist(FRIEND_USERNAME)).thenReturn(true);

        assertThrows(UserAlreadyHasFriendWithSuchUsernameException.class, () ->
                FriendAddingProcessor.addFriend(this.mockUserRepository, LOGGED_USERNAME, FRIEND_USERNAME),
            "Should throw exception when users are already friends"
        );

        verify(this.mockUserRepository, never()).updateUsers(any(User.class));
    }

    @Test
    void testAddSelfAsFriend() {
        String sameUser = "sameUser";

        assertThrows(UserCannotAddSelfAsFriend.class, () ->
                FriendAddingProcessor.addFriend(this.mockUserRepository, sameUser, sameUser),
            "Should throw exception when trying to add self as friend"
        );

        verify(this.mockUserRepository, never()).findUserByUsername(anyString());
        verify(this.mockUserRepository, never()).updateUsers(any(User.class));
    }

    @Test
    void testAddFriendWithNullLoggedUsername() {
        assertThrows(UserDataMustContainAtLeastOneNonBlankSymbolException.class, () ->
                FriendAddingProcessor.addFriend(this.mockUserRepository, null, FRIEND_USERNAME),
            "Should throw UserDataMustContainAtLeastOneNonBlankSymbol for null logged username"
        );
    }

    @Test
    void testAddFriendWithBlankUsername() {
        assertThrows(UserDataMustContainAtLeastOneNonBlankSymbolException.class, () ->
                FriendAddingProcessor.addFriend(this.mockUserRepository, "       ", FRIEND_USERNAME),
            "Should throw UserDataMustContainAtLeastOneNonBlankSymbolException for blank friend username"
        );
    }

    @Test
    void testAddFriendWithNullFriendUsername() {
        assertThrows(UserDataMustContainAtLeastOneNonBlankSymbolException.class, () ->
                FriendAddingProcessor.addFriend(this.mockUserRepository, LOGGED_USERNAME, null),
            "Should throw UserDataMustContainAtLeastOneNonBlankSymbol for null friend username"
        );
    }

    @Test
    void testAddFriendWithBlankFriendUsername() {
        assertThrows(UserDataMustContainAtLeastOneNonBlankSymbolException.class, () ->
                FriendAddingProcessor.addFriend(this.mockUserRepository, LOGGED_USERNAME, "     "),
            "Should throw UserDataMustContainAtLeastOneNonBlankSymbolException for blank friend username"
        );
    }

    @Test
    void testAddFriendDuplicateCalls() throws Exception {
        when(this.mockUserRepository.findUserByUsername(LOGGED_USERNAME)).thenReturn(this.loggedUser);
        when(this.mockUserRepository.findUserByUsername(FRIEND_USERNAME)).thenReturn(this.friendUser);
        when(this.mockUserRepository.doesUserExist(LOGGED_USERNAME)).thenReturn(true);
        when(this.mockUserRepository.doesUserExist(FRIEND_USERNAME)).thenReturn(true);

        FriendAddingProcessor.addFriend(this.mockUserRepository, LOGGED_USERNAME, FRIEND_USERNAME);

        assertThrows(UserAlreadyHasFriendWithSuchUsernameException.class, () ->
                FriendAddingProcessor.addFriend(this.mockUserRepository, LOGGED_USERNAME, FRIEND_USERNAME),
            "Should throw UserAlreadyHasFriendWithSuchUsernameException on duplicate add friend call"
        );
    }

    private UserRepository mockUserRepository;

    private static final String LOGGED_USERNAME = "username";
    private static final String FRIEND_USERNAME = "friendUsername";
    private static final String NON_EXISTENT_USERNAME = "nonExistentUser";

    private User loggedUser;
    private User friendUser;

}