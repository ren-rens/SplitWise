package bg.sofia.uni.fmi.mjt.splitwise.server.repositories.splitwise.processors.payments;

import bg.sofia.uni.fmi.mjt.splitwise.exceptions.GroupWithThisNameDoesNotExistException;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.NoFriendWithSuchUsernameExistsException;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.UserWithThisUsernameDoesNotExistException;
import bg.sofia.uni.fmi.mjt.splitwise.server.model.requests.UserSplitRequest;
import bg.sofia.uni.fmi.mjt.splitwise.server.model.user.User;
import bg.sofia.uni.fmi.mjt.splitwise.server.repositories.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class SplitProcessorTest {

    @BeforeEach
    void setUp() {
        this.mockUserRepository = mock(UserRepository.class);
        this.loggedUser = new User(LOGGED_USERNAME, "password");
        this.friendUser = new User(FRIEND_USERNAME, "password");
        this.groupMemberUser = new User(GROUP_MEMBER_USERNAME, "password");
    }

    @Test
    void testSplitWithLoggedUserDoesNotExist() {
        when(this.mockUserRepository.findUserByUsername(NON_EXISTENT_USERNAME))
            .thenReturn(null);

        UserSplitRequest request = new UserSplitRequest(NON_EXISTENT_USERNAME, AMOUNT,
            FRIEND_USERNAME, REASON, false);

        assertThrows(UserWithThisUsernameDoesNotExistException.class, () ->
                SplitProcessor.split(this.mockUserRepository, request),
            "When trying to split with non-existent logged user should throw exception"
        );

        verify(this.mockUserRepository, never()).updateUsers(any());
    }

    @Test
    void testSplitBetweenFriendsWithFriendDoesNotExist() {
        when(this.mockUserRepository.findUserByUsername(LOGGED_USERNAME)).thenReturn(this.loggedUser);
        when(this.mockUserRepository.findUserByUsername(NON_EXISTENT_USERNAME)).thenReturn(null);

        UserSplitRequest request = new UserSplitRequest(LOGGED_USERNAME, AMOUNT,
            NON_EXISTENT_USERNAME, REASON, false);

        assertThrows(NoFriendWithSuchUsernameExistsException.class, () ->
                SplitProcessor.split(this.mockUserRepository, request),
            "When trying to split with non-existent friend should throw exception"
        );

        verify(this.mockUserRepository, never()).updateUsers(any());
    }

    @Test
    void testSplitBetweenFriendsSuccessfully() throws Exception {
        this.loggedUser.addFriend(FRIEND_USERNAME);
        this.friendUser.addFriend(LOGGED_USERNAME);

        when(this.mockUserRepository.findUserByUsername(LOGGED_USERNAME)).thenReturn(this.loggedUser);
        when(this.mockUserRepository.findUserByUsername(FRIEND_USERNAME)).thenReturn(this.friendUser);

        UserSplitRequest request = new UserSplitRequest(LOGGED_USERNAME, AMOUNT,
            FRIEND_USERNAME, REASON, false);

        SplitProcessor.split(this.mockUserRepository, request);

        verify(this.mockUserRepository, times(2)).updateUsers(any(User.class));
        verify(this.mockUserRepository).updateUsers(argThat(user ->
            user.getUsername().equals(LOGGED_USERNAME)));
        verify(this.mockUserRepository).updateUsers(argThat(user ->
            user.getUsername().equals(FRIEND_USERNAME)));
    }

    @Test
    void testSplitBetweenFriendsWithMultipleFriends() throws Exception {
        User friend2 = new User("friend2", "password");
        this.loggedUser.addFriend(FRIEND_USERNAME);
        this.loggedUser.addFriend("friend2");
        this.friendUser.addFriend(LOGGED_USERNAME);
        friend2.addFriend(LOGGED_USERNAME);

        when(this.mockUserRepository.findUserByUsername(LOGGED_USERNAME)).thenReturn(this.loggedUser);
        when(this.mockUserRepository.findUserByUsername(FRIEND_USERNAME)).thenReturn(this.friendUser);
        when(this.mockUserRepository.findUserByUsername("friend2")).thenReturn(friend2);

        UserSplitRequest request = new UserSplitRequest(LOGGED_USERNAME, AMOUNT,
            FRIEND_USERNAME, REASON, false);

        SplitProcessor.split(this.mockUserRepository, request);

        verify(this.mockUserRepository, times(2)).updateUsers(any(User.class));
    }

    @Test
    void testSplitBetweenGroupWithGroupDoesNotExist() {
        when(this.mockUserRepository.findUserByUsername(LOGGED_USERNAME)).thenReturn(this.loggedUser);

        UserSplitRequest request = new UserSplitRequest(LOGGED_USERNAME, AMOUNT,
            NON_EXISTENT_GROUP, REASON, true);

        assertThrows(GroupWithThisNameDoesNotExistException.class, () ->
                SplitProcessor.split(this.mockUserRepository, request),
            "When trying to split with non-existent group should throw exception"
        );

        verify(this.mockUserRepository, never()).updateUsers(any());
    }

    @Test
    void testSplitBetweenGroupSuccessfully() throws Exception {
        this.loggedUser.addGroup(GROUP_NAME, Set.of(GROUP_MEMBER_USERNAME));
        this.groupMemberUser.addGroup(GROUP_NAME, Set.of(LOGGED_USERNAME));

        when(this.mockUserRepository.findUserByUsername(LOGGED_USERNAME)).thenReturn(this.loggedUser);
        when(this.mockUserRepository.findUserByUsername(GROUP_MEMBER_USERNAME)).thenReturn(this.groupMemberUser);

        UserSplitRequest request = new UserSplitRequest(LOGGED_USERNAME, AMOUNT,
            GROUP_NAME, REASON, true);

        SplitProcessor.split(this.mockUserRepository, request);

        verify(this.mockUserRepository, atLeast(2)).updateUsers(any(User.class));
        verify(this.mockUserRepository).updateUsers(argThat(user ->
            user.getUsername().equals(LOGGED_USERNAME)));
        verify(this.mockUserRepository).updateUsers(argThat(user ->
            user.getUsername().equals(GROUP_MEMBER_USERNAME)));
    }

    private UserRepository mockUserRepository;
    private User loggedUser;
    private User friendUser;
    private User groupMemberUser;

    private static final String LOGGED_USERNAME = "loggedUser";
    private static final String FRIEND_USERNAME = "friendUser";
    private static final String GROUP_MEMBER_USERNAME = "groupMember";
    private static final String NON_EXISTENT_USERNAME = "nonExistentUser";
    private static final String GROUP_NAME = "testGroup";
    private static final String NON_EXISTENT_GROUP = "nonExistentGroup";
    private static final String REASON = "dinner";
    private static final double AMOUNT = 100.0;
}