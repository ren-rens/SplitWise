package bg.sofia.uni.fmi.mjt.splitwise.server.repositories.splitwise.processors.payments;

import bg.sofia.uni.fmi.mjt.splitwise.exceptions.GroupWithThisNameAlreadyExistsException;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.GroupWithThisNameDoesNotExistException;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.NoFriendWithSuchUsernameExistsException;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.UserAlreadyHasFriendWithSuchUsernameException;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.UserWithThisUsernameDoesNotExistException;
import bg.sofia.uni.fmi.mjt.splitwise.server.model.user.User;
import bg.sofia.uni.fmi.mjt.splitwise.server.repositories.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class PaymentProcessorTest {

    @BeforeEach
    void setUp() {
        this.mockUserRepository = mock(UserRepository.class);
        this.loggedUser = new User(LOGGED_USERNAME, "password");
        this.friendUser = new User(FRIEND_USERNAME, "password");
        this.groupMemberUser = new User(GROUP_MEMBER_USERNAME, "password");
    }

    @Test
    void testPayedWithNullLoggedUsername() {
        assertThrows(UserWithThisUsernameDoesNotExistException.class, () ->
                PaymentProcessor.payed(this.mockUserRepository, null, null, AMOUNT, FRIEND_USERNAME),
            "When trying to pay but the logged username is null should throw UserWithThisUsernameDoesNotExistException"
        );
    }

    @Test
    void testPayedWithNullFriendUsername() {
        when(this.mockUserRepository.findUserByUsername(LOGGED_USERNAME)).thenReturn(this.loggedUser);

        assertThrows(UserWithThisUsernameDoesNotExistException.class, () ->
                PaymentProcessor.payed(this.mockUserRepository, LOGGED_USERNAME, null, AMOUNT, null),
            "When trying to pay but the friend username is null should throw UserWithThisUsernameDoesNotExistException"
        );

        verify(this.mockUserRepository, never()).updateUsers(any());
    }

    @Test
    void testPayedWithLoggedUserDoesNotExist() {
        when(this.mockUserRepository.findUserByUsername(NON_EXISTENT_USERNAME))
            .thenReturn(null);

        assertThrows(UserWithThisUsernameDoesNotExistException.class, () ->
                PaymentProcessor.payed(this.mockUserRepository, NON_EXISTENT_USERNAME, null, AMOUNT, FRIEND_USERNAME),
            "When trying to pay with non-existent logged user should throw exception"
        );

        verify(this.mockUserRepository, never()).updateUsers(any());
    }

    @Test
    void testPayedFriendWithFriendDoesNotExist() {
        when(this.mockUserRepository.findUserByUsername(LOGGED_USERNAME)).thenReturn(this.loggedUser);
        when(this.mockUserRepository.findUserByUsername(NON_EXISTENT_USERNAME))
            .thenReturn(null);

        assertThrows(UserWithThisUsernameDoesNotExistException.class, () ->
                PaymentProcessor.payed(this.mockUserRepository, LOGGED_USERNAME, null, AMOUNT, NON_EXISTENT_USERNAME),
            "When trying to pay non-existent friend should throw exception"
        );

        verify(this.mockUserRepository, never()).updateUsers(any());
    }

    @Test
    void testPayedFriendFromGroupWithFriendDoesNotExist() throws GroupWithThisNameAlreadyExistsException {
        when(this.mockUserRepository.findUserByUsername(LOGGED_USERNAME)).thenReturn(this.loggedUser);
        when(this.mockUserRepository.findUserByUsername(NON_EXISTENT_USERNAME))
            .thenReturn(null);

        this.loggedUser.addGroup(GROUP_NAME, Set.of(NON_EXISTENT_USERNAME));

        assertThrows(UserWithThisUsernameDoesNotExistException.class, () ->
                PaymentProcessor.payed(this.mockUserRepository, LOGGED_USERNAME, GROUP_NAME, AMOUNT, NON_EXISTENT_USERNAME),
            "When trying to pay non-existent group member should throw exception"
        );

        verify(this.mockUserRepository, never()).updateUsers(any());
    }

    @Test
    void testPayedFriendFromGroupWhenNotInGroup() throws GroupWithThisNameAlreadyExistsException {
        this.loggedUser.addGroup(GROUP_NAME, Set.of("otherMember"));
        this.groupMemberUser.addGroup(GROUP_NAME, Set.of(LOGGED_USERNAME));

        when(this.mockUserRepository.findUserByUsername(LOGGED_USERNAME)).thenReturn(this.loggedUser);
        when(this.mockUserRepository.findUserByUsername(GROUP_MEMBER_USERNAME)).thenReturn(this.groupMemberUser);

        assertThrows(NoFriendWithSuchUsernameExistsException.class, () ->
                PaymentProcessor.payed(this.mockUserRepository, LOGGED_USERNAME, GROUP_NAME, AMOUNT, GROUP_MEMBER_USERNAME),
            "When paying group member who is not in the group should throw NoFriendWithSuchUsernameExistsException"
        );
    }

    @Test
    void testPayedFriendFromGroupSuccessfully()
        throws UserWithThisUsernameDoesNotExistException, NoFriendWithSuchUsernameExistsException,
        GroupWithThisNameDoesNotExistException, GroupWithThisNameAlreadyExistsException {
        this.loggedUser.addGroup(GROUP_NAME, Set.of(GROUP_MEMBER_USERNAME));
        this.groupMemberUser.addGroup(GROUP_NAME, Set.of(LOGGED_USERNAME));

        when(this.mockUserRepository.findUserByUsername(LOGGED_USERNAME)).thenReturn(this.loggedUser);
        when(this.mockUserRepository.findUserByUsername(GROUP_MEMBER_USERNAME)).thenReturn(this.groupMemberUser);

        PaymentProcessor.payed(this.mockUserRepository, LOGGED_USERNAME, GROUP_NAME, AMOUNT, GROUP_MEMBER_USERNAME);

        verify(this.mockUserRepository, times(3)).updateUsers(any(User.class));
    }

    @Test
    void testPayedFriendSuccessfully()
        throws UserWithThisUsernameDoesNotExistException, NoFriendWithSuchUsernameExistsException,
        GroupWithThisNameDoesNotExistException, UserAlreadyHasFriendWithSuchUsernameException {
        this.loggedUser.addFriend(FRIEND_USERNAME);
        this.friendUser.addFriend(LOGGED_USERNAME);

        when(this.mockUserRepository.findUserByUsername(LOGGED_USERNAME)).thenReturn(this.loggedUser);
        when(this.mockUserRepository.findUserByUsername(FRIEND_USERNAME)).thenReturn(this.friendUser);

        PaymentProcessor.payed(this.mockUserRepository, LOGGED_USERNAME, null, AMOUNT, FRIEND_USERNAME);

        verify(this.mockUserRepository, times(3)).updateUsers(any(User.class));
    }

    @Test
    void testPayedFriendWhenNotFriends() {
        when(this.mockUserRepository.findUserByUsername(LOGGED_USERNAME)).thenReturn(this.loggedUser);
        when(this.mockUserRepository.findUserByUsername(FRIEND_USERNAME)).thenReturn(this.friendUser);

        assertThrows(NoFriendWithSuchUsernameExistsException.class, () ->
                PaymentProcessor.payed(this.mockUserRepository, LOGGED_USERNAME, null, AMOUNT, FRIEND_USERNAME),
            "When trying to pay non-friend should throw NoFriendWithSuchUsernameExistsException"
        );
    }

    @Test
    void testPayedFriendMultipleTimes()
        throws UserWithThisUsernameDoesNotExistException, NoFriendWithSuchUsernameExistsException,
        GroupWithThisNameDoesNotExistException, UserAlreadyHasFriendWithSuchUsernameException {
        this.loggedUser.addFriend(FRIEND_USERNAME);
        this.friendUser.addFriend(LOGGED_USERNAME);

        when(this.mockUserRepository.findUserByUsername(LOGGED_USERNAME)).thenReturn(this.loggedUser);
        when(this.mockUserRepository.findUserByUsername(FRIEND_USERNAME)).thenReturn(this.friendUser);

        PaymentProcessor.payed(this.mockUserRepository, LOGGED_USERNAME, null, AMOUNT, FRIEND_USERNAME);
        PaymentProcessor.payed(this.mockUserRepository, LOGGED_USERNAME, null, AMOUNT, FRIEND_USERNAME);
        PaymentProcessor.payed(this.mockUserRepository, LOGGED_USERNAME, null, AMOUNT, FRIEND_USERNAME);

        verify(this.mockUserRepository, times(9)).updateUsers(any(User.class));
    }

    @Test
    void testPayedFriendWithBlankFriendUsername() {
        when(this.mockUserRepository.findUserByUsername(LOGGED_USERNAME)).thenReturn(this.loggedUser);
        when(this.mockUserRepository.findUserByUsername("   ")).thenReturn(null);

        assertThrows(UserWithThisUsernameDoesNotExistException.class, () ->
                PaymentProcessor.payed(this.mockUserRepository, LOGGED_USERNAME, null, AMOUNT, "   "),
            "When trying to pay with blank friend username should throw exception"
        );
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
    private static final double AMOUNT = 50.0;

}