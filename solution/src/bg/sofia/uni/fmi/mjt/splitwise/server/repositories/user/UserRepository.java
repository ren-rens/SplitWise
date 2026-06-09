package bg.sofia.uni.fmi.mjt.splitwise.server.repositories.user;

import bg.sofia.uni.fmi.mjt.splitwise.exceptions.UserWIthThisUsernameDoesNotHaveAnyPaymentHistory;
import bg.sofia.uni.fmi.mjt.splitwise.server.model.payments.PaymentHistory;
import bg.sofia.uni.fmi.mjt.splitwise.server.model.user.User;
import bg.sofia.uni.fmi.mjt.splitwise.server.repositories.user.processors.PaymentHistoryProcessor;
import bg.sofia.uni.fmi.mjt.splitwise.server.repositories.user.processors.UserProcessor;

import java.util.HashMap;
import java.util.Map;

public class UserRepository {

    public UserRepository() {
        this(DEFAULT_USERS_FILE, DEFAULT_PAYMENT_HISTORY_FILE);
    }

    /**
     * testing constructor needed only for the unit tests
     **/
    UserRepository(String usersFile, String paymentHistoryFile) {
        this.usersFile = usersFile;
        this.paymentHistoryFile = paymentHistoryFile;
        this.paymentHistory = new HashMap<>();
        this.registeredUsers = new HashMap<>();

        loadUsersFromFile();
        loadPaymentHistory();
    }

    private void loadUsersFromFile() {
        UserProcessor.loadUsersFromFile(this.usersFile, this.registeredUsers);
    }

    private void loadPaymentHistory() {
        PaymentHistoryProcessor.loadPaymentHistory(this.paymentHistoryFile, this.paymentHistory);
    }

    public synchronized void saveUser(User user) {
        UserProcessor.saveUser(this.usersFile, this.registeredUsers, user);
    }

    public synchronized boolean doesUserExist(String username) {
        return UserProcessor.doesUserExist(this.registeredUsers, username);
    }

    public synchronized User findUserByUsername(String username) {
        return UserProcessor.findUserByUsername(this.registeredUsers, username);
    }

    public synchronized void updateUsers(User user) {
        UserProcessor.updateUsers(this.usersFile, this.registeredUsers, user);
    }

    public synchronized void updatePaymentHistory(User user, String target) {
        PaymentHistoryProcessor.updatePaymentHistory(this.paymentHistoryFile, this.paymentHistory, user, target);
    }

    public String getFormattedPaymentHistoryByUsername(String username)
        throws UserWIthThisUsernameDoesNotHaveAnyPaymentHistory {
        return PaymentHistoryProcessor.getFormattedPaymentHistoryByUsername(this.paymentHistory, username);
    }


    public static final String DEFAULT_PAYMENT_HISTORY_FILE = "payment_history_for_users.bin";
    public static final String DEFAULT_USERS_FILE = "splitwise_users.bin";

    private Map<String, User> registeredUsers;
    private Map<String, PaymentHistory> paymentHistory;

    public final String paymentHistoryFile;
    public final String usersFile;

}