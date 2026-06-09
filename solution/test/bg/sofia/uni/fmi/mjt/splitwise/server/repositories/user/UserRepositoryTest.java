package bg.sofia.uni.fmi.mjt.splitwise.server.repositories.user;

import bg.sofia.uni.fmi.mjt.splitwise.exceptions.NoFriendWithSuchUsernameExistsException;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.UserAlreadyHasFriendWithSuchUsernameException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import bg.sofia.uni.fmi.mjt.splitwise.exceptions.UserWIthThisUsernameDoesNotHaveAnyPaymentHistory;
import bg.sofia.uni.fmi.mjt.splitwise.server.model.user.User;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserRepositoryTest {

    @BeforeEach
    void setUp() {
        this.usersFile = this.tempDir.resolve("test_users.bin").toString();
        this.paymentHistoryFile = this.tempDir.resolve("test_payment_history.bin").toString();
        this.repository = new UserRepository(this.usersFile, this.paymentHistoryFile);
    }

    @Test
    void testSaveUserThatDidNotExistBefore() {
        User user = new User(USERNAME, PASSWORD);

        this.repository.saveUser(user);

        assertTrue(this.repository.doesUserExist(USERNAME),
            "When testing save user with user that did not exist before" +
                "must just save it in the map and the file");
    }

    @Test
    void testDoesUserExistWithNonExistentUser() {
        assertFalse(this.repository.doesUserExist("nonexistent"),
            "When testing does user exist with user that does not exist" +
                "must return false");
    }

    @Test
    void testFindUserByUsernameWithExistingUser() {
        User user = new User(USERNAME, PASSWORD);
        this.repository.saveUser(user);

        User found = this.repository.findUserByUsername(USERNAME);

        assertEquals(USERNAME, found.getUsername(),
            "When testing find user by username with user that exist in the system" +
                "must just give the user");
    }

    @Test
    void testUpdateUsersWithUserSavedInTheSystem() throws UserAlreadyHasFriendWithSuchUsernameException {
        User user = new User(USERNAME, PASSWORD);
        this.repository.saveUser(user);

        user.addFriend(FRIEND_USERNAME);

        this.repository.updateUsers(user);

        User updated = this.repository.findUserByUsername(USERNAME);
        assertFalse(updated.getFriends().isEmpty(),
            "When testing update user with user that was saved before in the system" +
                "must reflect all changes on him and them save them again");
    }

    @Test
    void testUpdatePaymentHistoryWIthNewUser()
        throws UserAlreadyHasFriendWithSuchUsernameException, NoFriendWithSuchUsernameExistsException {
        User user = new User(USERNAME, PASSWORD);
        user.addFriend(FRIEND_USERNAME);
        double amount = -10.00;
        user.splitWithFriend(amount, FRIEND_USERNAME, "reason for payment");
        this.repository.saveUser(user);

        this.repository.updatePaymentHistory(user, FRIEND_USERNAME);

        assertDoesNotThrow(() ->
            repository.getFormattedPaymentHistoryByUsername(USERNAME),
            "When testing update payment history for user that had no saved payments" +
                "must just save them in the map and the file"
        );
    }

    @Test
    void testGetFormattedPaymentHistoryOfNonExistentUser() {
        assertThrows(
            UserWIthThisUsernameDoesNotHaveAnyPaymentHistory.class,
            () -> this.repository.getFormattedPaymentHistoryByUsername("nonexistent"),
            "When testing get formatted payment history of non-existent user with" +
                "must throw exception"
        );
    }

    @TempDir
    Path tempDir;

    private UserRepository repository;
    private String usersFile;
    private String paymentHistoryFile;

    private static final String USERNAME = "username";
    private static final String FRIEND_USERNAME = "friend";
    private static final String PASSWORD = "password";

}