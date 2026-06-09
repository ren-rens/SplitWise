package bg.sofia.uni.fmi.mjt.splitwise.server.repositories.user.processors;

import bg.sofia.uni.fmi.mjt.splitwise.exceptions.GroupWithThisNameAlreadyExistsException;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.NoFriendWithSuchUsernameExistsException;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.UserAlreadyHasFriendWithSuchUsernameException;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.UserWIthThisUsernameDoesNotHaveAnyPaymentHistory;
import bg.sofia.uni.fmi.mjt.splitwise.server.model.payments.PaymentHistory;
import bg.sofia.uni.fmi.mjt.splitwise.server.model.payments.debt.Debt;
import bg.sofia.uni.fmi.mjt.splitwise.server.model.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PaymentHistoryProcessorTest {

    @BeforeEach
    void setUp() {
        this.paymentHistory = new HashMap<>();
        this.tempFile = tempDir.resolve("test_payment_history.bin").toString();
    }

    @Test
    void testLoadPaymentHistoryWithEmptyFile() {
        assertDoesNotThrow(() ->
            PaymentHistoryProcessor.loadPaymentHistory(this.tempFile, this.paymentHistory),
            "When testing loadPaymentHistory from empty file" +
                "should do nothing");
        assertTrue(this.paymentHistory.isEmpty());
    }

    @Test
    void testLoadPaymentHistoryWithSavedDataInTheFile()
        throws NoFriendWithSuchUsernameExistsException, UserAlreadyHasFriendWithSuchUsernameException {
        User user = createTestUserWithFriend();
        PaymentHistoryProcessor.updatePaymentHistory(this.tempFile, this.paymentHistory,
            user, FRIEND_USERNAME);

        Map<String, PaymentHistory> newPaymentHistory = new HashMap<>();
        PaymentHistoryProcessor.loadPaymentHistory(this.tempFile, newPaymentHistory);

        assertTrue(newPaymentHistory.containsKey(USERNAME));
        PaymentHistory loadedHistory = newPaymentHistory.get(USERNAME);
        assertNotNull(loadedHistory);
        assertEquals(USERNAME, loadedHistory.getSplitterOfDebt());
    }

    @Test
    void testUpdatePaymentHistoryWithUserWithNoPaymentHistory() {
        User user = new User(USERNAME, PASSWORD);
        PaymentHistoryProcessor.updatePaymentHistory(this.tempFile, this.paymentHistory,
            user, FRIEND_USERNAME);

        assertTrue(this.paymentHistory.containsKey(USERNAME),
            "When testing updatePaymentHistory with valid data" +
                "should save the user in the file and in the data of the repository");
    }

    @Test
    void testUpdatePaymentHistoryWithUserWithExistingPaymentHistoryWithFriends()
        throws NoFriendWithSuchUsernameExistsException, UserAlreadyHasFriendWithSuchUsernameException {
        User user = createTestUserWithFriend();

        PaymentHistoryProcessor.updatePaymentHistory(this.tempFile, this.paymentHistory,
            user, FRIEND_USERNAME);

        user.splitWithFriend(-30.00, FRIEND_USERNAME, REASON_FOR_PAYMENT);
        PaymentHistoryProcessor.updatePaymentHistory(this.tempFile, this.paymentHistory,
            user, FRIEND_USERNAME);

        PaymentHistory history = paymentHistory.get(USERNAME);
        List<Debt> payments = history.getPayments().get(FRIEND_USERNAME);
        assertEquals(2, payments.size());
    }

    @Test
    void testUpdatePaymentHistoryWithUserThatHasExistingPaymentHistoryInGroup() throws GroupWithThisNameAlreadyExistsException {
        User user = createTestUserWithGroup();

        PaymentHistoryProcessor.updatePaymentHistory(this.tempFile, this.paymentHistory,
            user, GROUP_NAME);

        assertTrue(paymentHistory.containsKey(USERNAME));
        PaymentHistory history = paymentHistory.get(USERNAME);
        assertNotNull(history.getPayments().get(GROUP_NAME));
    }

    @Test
    void testGetFormattedPaymentHistoryByUsernameThrowingWhenUserNotFound() {
        assertThrows(
            UserWIthThisUsernameDoesNotHaveAnyPaymentHistory.class, () ->
                PaymentHistoryProcessor.getFormattedPaymentHistoryByUsername(this.paymentHistory, "nonexistent"),
            "When testing getFormattedPaymentHistoryByUsername with a username that is non-existent" +
                "should throw exception"
        );
    }

    @Test
    void testGetFormattedPaymentHistoryWithFriendHistory()
        throws UserWIthThisUsernameDoesNotHaveAnyPaymentHistory, NoFriendWithSuchUsernameExistsException,
        UserAlreadyHasFriendWithSuchUsernameException {
        User user = createTestUserWithFriend();
        PaymentHistoryProcessor.updatePaymentHistory(this.tempFile, this.paymentHistory,
            user, FRIEND_USERNAME);

        String result = PaymentHistoryProcessor.getFormattedPaymentHistoryByUsername(
            paymentHistory, USERNAME
        );

        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    void testGetFormattedPaymentHistoryWithSavedPaymentHistoryInFile()
        throws UserWIthThisUsernameDoesNotHaveAnyPaymentHistory {
        PaymentHistory history = mock(PaymentHistory.class);
        when(history.paymentsFormatter()).thenReturn("Formatted output");
        this.paymentHistory.put(USERNAME, history);

        String result = PaymentHistoryProcessor.getFormattedPaymentHistoryByUsername(
            this.paymentHistory, USERNAME
        );

        assertEquals("Formatted output", result);
        verify(history).paymentsFormatter();
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
    private Map<String, PaymentHistory> paymentHistory;

    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";
    private static final String FRIEND_USERNAME = "friendUsername";

    private static final String GROUP_NAME = "groupName";
    private static final String REASON_FOR_PAYMENT = "reason for payment";

    private static final double AMOUNT = -50.00;

}
