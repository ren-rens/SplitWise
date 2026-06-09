package bg.sofia.uni.fmi.mjt.splitwise.server.repositories.splitwise.processors.data;

import bg.sofia.uni.fmi.mjt.splitwise.exceptions.UserWithThisUsernameDoesNotExistException;
import bg.sofia.uni.fmi.mjt.splitwise.server.model.group.Group;
import bg.sofia.uni.fmi.mjt.splitwise.server.model.friend.Friend;
import bg.sofia.uni.fmi.mjt.splitwise.server.model.user.User;
import bg.sofia.uni.fmi.mjt.splitwise.server.repositories.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class UserDataGettingProcessorTest {

    @BeforeEach
    void setUp() {
        this.mockUserRepository = mock(UserRepository.class);
        this.user = new User(USERNAME, "password");
    }

    @Test
    void testGetFriendsOfUserWithUserDoesNotExist() {
        when(this.mockUserRepository.findUserByUsername(NON_EXISTENT_USERNAME))
            .thenReturn(null);

        assertThrows(UserWithThisUsernameDoesNotExistException.class, () ->
                UserDataGettingProcessor.getFriendsOfUser(this.mockUserRepository, NON_EXISTENT_USERNAME),
            "Should throw exception when user does not exist"
        );
    }

    @Test
    void testGetFriendsOfUserSuccessfully() throws Exception {
        this.user.addFriend("friend1");
        this.user.addFriend("friend2");

        when(this.mockUserRepository.findUserByUsername(USERNAME)).thenReturn(this.user);

        String result = UserDataGettingProcessor.getFriendsOfUser(this.mockUserRepository, USERNAME);
        assertNotNull(result,
            "When testing getFriendOfUser with saved friend" +
                " should return a formatted list of friends");
        verify(this.mockUserRepository).findUserByUsername(USERNAME);
    }

    @Test
    void testGetFriendsOfUserWithNullUsername() {
        assertThrows(UserWithThisUsernameDoesNotExistException.class, () ->
                UserDataGettingProcessor.getFriendsOfUser(this.mockUserRepository, null),
            "Should throw UserWithThisUsernameDoesNotExistException for null username"
        );
    }

    @Test
    void testGetFriendsOfUserWithNoFriends() throws Exception {
        User userWithoutFriends = new User("lonelyUser", "password");
        when(this.mockUserRepository.findUserByUsername("lonelyUser")).thenReturn(userWithoutFriends);

        String result = UserDataGettingProcessor.getFriendsOfUser(this.mockUserRepository, "lonelyUser");

        assertNotNull(result, "When testing getFriendsOfUser with user with no friends" +
            "should return non-null but empty formatted list of friends");
        verify(this.mockUserRepository).findUserByUsername("lonelyUser");
    }

    @Test
    void testGetGroupsOfUserWithUserDoesNotExist() {
        when(this.mockUserRepository.findUserByUsername(NON_EXISTENT_USERNAME))
            .thenReturn(null);

        assertThrows(UserWithThisUsernameDoesNotExistException.class, () ->
                UserDataGettingProcessor.getGroupsOfUser(this.mockUserRepository, NON_EXISTENT_USERNAME),
            "Should throw exception when user does not exist"
        );
    }

    @Test
    void testGetGroupsOfUserWithNullUsername() {
        assertThrows(UserWithThisUsernameDoesNotExistException.class, () ->
                UserDataGettingProcessor.getGroupsOfUser(this.mockUserRepository, null),
            "Should throw exception for null username"
        );
    }

    @Test
    void testGetGroupsOfUserWithNoGroups() throws Exception {
        User userWithoutGroups = new User("noGroupsUser", "password");
        when(this.mockUserRepository.findUserByUsername("noGroupsUser")).thenReturn(userWithoutGroups);

        String result = UserDataGettingProcessor.getGroupsOfUser(this.mockUserRepository, "noGroupsUser");

        assertNotNull(result, "When testing getFriendsOfUser with user with no groups" +
            "should return non-null but empty formatted list of groups");
        verify(this.mockUserRepository).findUserByUsername("noGroupsUser");
    }

    @Test
    void testGetGroupsOfUserWithMultipleFriendsInGroup() throws Exception {
        User userWithFriends = mock(User.class);

        when(this.mockUserRepository.findUserByUsername(USERNAME))
            .thenReturn(userWithFriends);

        Map<String, Friend> friends = new HashMap<>();
        friends.put("friend1", mock(Friend.class));
        friends.put("friend2", mock(Friend.class));

        Group group = new Group(USERNAME, "GroupName", friends);
        when(userWithFriends.getGroups())
            .thenReturn(Map.of("GroupName", group));

        String result = UserDataGettingProcessor.getGroupsOfUser(this.mockUserRepository, USERNAME);

        assertNotNull(result,
            "When testing getGroups with user that has groups" +
                "should return formatted list of groups");
        verify(this.mockUserRepository).findUserByUsername(USERNAME);
    }

    private UserRepository mockUserRepository;
    private User user;

    private static final String USERNAME = "username";
    private static final String NON_EXISTENT_USERNAME = "nonExistentUser";
}