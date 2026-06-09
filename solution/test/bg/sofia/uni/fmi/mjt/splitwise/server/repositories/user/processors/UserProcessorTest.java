package bg.sofia.uni.fmi.mjt.splitwise.server.repositories.user.processors;

import bg.sofia.uni.fmi.mjt.splitwise.exceptions.GroupWithThisNameAlreadyExistsException;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.NoFriendWithSuchUsernameExistsException;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.UserAlreadyHasFriendWithSuchUsernameException;
import bg.sofia.uni.fmi.mjt.splitwise.server.model.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UserProcessorTest {

    @BeforeEach
    void setUp() {
        this.registeredUsers = new HashMap<>();
        this.tempFile = this.tempDir.resolve("test_splitwise_users.bin").toString();
    }

    @Test
    void testLoadUsersWithEmptyFile() {
        assertDoesNotThrow(() ->
                UserProcessor.loadUsersFromFile(this.tempFile, this.registeredUsers),
            "When testing loadUsers from empty file" +
                "should do nothing");
        assertTrue(this.registeredUsers.isEmpty());
    }

    @Test
    void testLoadUsersWithSavedDataInTheFile()
        throws NoFriendWithSuchUsernameExistsException, UserAlreadyHasFriendWithSuchUsernameException {
        User user = createTestUserWithFriend();
        UserProcessor.updateUsers(this.tempFile, this.registeredUsers,
            user);

        Map<String, User> newUser = new HashMap<>();
        UserProcessor.loadUsersFromFile(this.tempFile, newUser);

        assertTrue(newUser.containsKey(USERNAME),
            "When testing loadUsers with file that has saved data within" +
                "should load the data in the map correctly");
    }

    @Test
    void testUpdateUserWithUserWithNoUser() {
        User user = new User(USERNAME, PASSWORD);
        UserProcessor.updateUsers(this.tempFile, this.registeredUsers, user);

        assertTrue(this.registeredUsers.containsKey(USERNAME),
            "When testing updateUser with valid data" +
                "should save the user in the file and in the data of the repository");
    }

    @Test
    void testUpdateUserWithUserWithExistingUserWithFriends()
        throws NoFriendWithSuchUsernameExistsException, UserAlreadyHasFriendWithSuchUsernameException {
        User user = createTestUserWithFriend();
        UserProcessor.updateUsers(this.tempFile, this.registeredUsers, user);

        assertTrue(this.registeredUsers.containsKey(USERNAME),
            "When testing updateUser with existent user that has saved friends in his data" +
                "should update it successfully");
    }

    @Test
    void testUpdateUserWithUserThatHasExistingUserInGroup() throws
        GroupWithThisNameAlreadyExistsException {
        User user = createTestUserWithGroup();

        UserProcessor.updateUsers(this.tempFile, this.registeredUsers, user);

        assertTrue(this.registeredUsers.containsKey(USERNAME),
            "When testing updateUser with existent user that has saved groups in his data" +
                "should update it successfully");
        User updatedUser = this.registeredUsers.get(USERNAME);
        assertNotNull(updatedUser.getGroups().get(GROUP_NAME),
            "The user that was given must be non-null");
    }

    @Test
    void testSaveUserWithNoUserSavedBefore() {
        User user = new User(USERNAME, PASSWORD);
        UserProcessor.saveUser(this.tempFile, this.registeredUsers, user);

        assertTrue(this.registeredUsers.containsKey(USERNAME),
            "When testing saveUser with valid data" +
                "should save the user in the file and in the data of the repository");
    }

    @Test
    void testDoesUserExistWithNonExistentUser() {
        assertFalse(UserProcessor.doesUserExist(this.registeredUsers, USERNAME),
            "When testing doesUserExist with non-existent user" +
                "should return false");
    }

    @Test
    void testDoesUserExistWithExistentUser() {
        this.registeredUsers.put(USERNAME, new User(USERNAME, PASSWORD));
        assertTrue(UserProcessor.doesUserExist(this.registeredUsers, USERNAME),
            "When testing doesUserExist with existent user" +
                "should return true");
    }

    @Test
    void testFindUserByUsernameWithExistentUser() {
        this.registeredUsers.put(USERNAME, new User(USERNAME, PASSWORD));
        assertNotNull(UserProcessor.findUserByUsername(this.registeredUsers, USERNAME),
            "When testing doesUserExist with existent user" +
                "should return the user and not NULL");
    }

    @Test
    void testFindUserByUsernameWithNonExistentUser() {
        assertNull(UserProcessor.findUserByUsername(this.registeredUsers, USERNAME),
            "When testing doesUserExist with non-existent user" +
                "should return NULL");
    }

    private User createTestUserWithFriend()
        throws UserAlreadyHasFriendWithSuchUsernameException, NoFriendWithSuchUsernameExistsException {
        User user = new User(USERNAME, PASSWORD);
        user.addFriend(FRIEND_USERNAME);
        user.splitWithFriend(AMOUNT, FRIEND_USERNAME, REASON_FOR_PAYMENT);

        return user;
    }

    private User createTestUserWithGroup() throws GroupWithThisNameAlreadyExistsException {
        User user = new User(USERNAME, PASSWORD);
        user.addGroup(GROUP_NAME, Set.of(FRIEND_USERNAME));
        user.splitWithGroup(AMOUNT, GROUP_NAME, REASON_FOR_PAYMENT);

        return user;
    }

    @TempDir
    private Path tempDir;

    private String tempFile;
    private Map<String, User> registeredUsers;

    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";
    private static final String FRIEND_USERNAME = "friendUsername";

    private static final String GROUP_NAME = "groupName";
    private static final String REASON_FOR_PAYMENT = "reason for payment";

    private static final double AMOUNT = -50.00;

}
