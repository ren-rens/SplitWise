package bg.sofia.uni.fmi.mjt.splitwise.server.model.user;

import bg.sofia.uni.fmi.mjt.splitwise.exceptions.GroupWithThisNameAlreadyExistsException;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.GroupWithThisNameDoesNotExistException;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.NoFriendWithSuchUsernameExistsException;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.UserAlreadyHasFriendWithSuchUsernameException;
import bg.sofia.uni.fmi.mjt.splitwise.server.model.payments.debt.DebtConstants;
import bg.sofia.uni.fmi.mjt.splitwise.server.model.payments.Notification;
import bg.sofia.uni.fmi.mjt.splitwise.server.model.friend.Friend;
import bg.sofia.uni.fmi.mjt.splitwise.server.model.group.Group;
import bg.sofia.uni.fmi.mjt.splitwise.server.model.user.processors.AddFriendProcessor;
import bg.sofia.uni.fmi.mjt.splitwise.server.model.user.processors.AddGroupProcessor;
import bg.sofia.uni.fmi.mjt.splitwise.server.model.user.processors.payments.UserPayProcessor;
import bg.sofia.uni.fmi.mjt.splitwise.server.model.user.processors.payments.UserSplitProcessor;
import bg.sofia.uni.fmi.mjt.splitwise.server.model.user.processors.status.UserStatusProcessor;

import java.io.Serial;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class User implements Serializable {

    @Serial
    private static final long serialVersionUID = -7737678883550377070L;

    public User(String username, String passwordHash) {
        this.username = username;
        this.password = passwordHash;
        this.friends = new HashMap<>();
        this.groups = new HashMap<>();
        this.notifications = new Notification();
    }

    public String getUsername() {
        return this.username;
    }

    public String getPassword() {
        return this.password;
    }

    public Map<String, Friend> getFriends() {
        return Collections.unmodifiableMap(this.friends);
    }

    public Map<String, Group> getGroups() {
        return Collections.unmodifiableMap(this.groups);
    }

    public void addFriend(String username) throws UserAlreadyHasFriendWithSuchUsernameException {
        AddFriendProcessor.addFriend(this.friends, username);
    }

    public void addGroup(String groupName, Set<String> usernames) throws GroupWithThisNameAlreadyExistsException {
        AddGroupProcessor.addGroup(this.groups, this.username, groupName, usernames);
    }

    public void splitWithFriend(double amount, String username, String reasonForPayment)
        throws NoFriendWithSuchUsernameExistsException {
        UserSplitProcessor.splitWithFriend(this.friends, amount, username, reasonForPayment);
    }

    public void splitWithGroup(double amount, String groupName, String reasonForPayment) {
        UserSplitProcessor.splitWithGroup(this.groups, amount, groupName, reasonForPayment);
    }

    public void splitWithFriendInGroup(String groupName, String friendUsername,
                                       double amount, String reason)
        throws NoFriendWithSuchUsernameExistsException {
        UserSplitProcessor.splitWithFriendInGroup(this.groups, groupName, friendUsername, amount, reason);
    }

    public String getStatus() {
        return UserStatusProcessor.getStatus(this.friends, this.groups);
    }

    public void payFriend(String friendUsername, double amount)
        throws NoFriendWithSuchUsernameExistsException {
        UserPayProcessor.payFriend(this.friends, friendUsername,
            amount * DebtConstants.DEBT_MULTIPLIER);
    }

    public void receivePaymentFrom(String friendUsername, double amount)
        throws NoFriendWithSuchUsernameExistsException {
        UserPayProcessor.payFriend(this.friends, friendUsername, amount);
    }

    public void payFriendFromGroup(String groupName,
                                   String friendUsername,
                                   double amount)
        throws GroupWithThisNameDoesNotExistException, NoFriendWithSuchUsernameExistsException {
        UserPayProcessor.payFriendFromGroup(this.groups, groupName,
            friendUsername, amount * DebtConstants.DEBT_MULTIPLIER);
    }

    public void receivePaymentFromGroup(String groupName,
                                        String friendUsername,
                                        double amount)
        throws GroupWithThisNameDoesNotExistException, NoFriendWithSuchUsernameExistsException {
        UserPayProcessor.payFriendFromGroup(this.groups, groupName, friendUsername, amount);
    }

    public Notification getNotifications() {
        return this.notifications;
    }

    public void addMessageToNotifications(String message) {
        this.notifications.addMessage(message);
    }

    public void clearNotifications() {
        this.notifications.clearNotifications();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        User user = (User) o;
        return Objects.equals(username, user.username);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(username);
    }

    private final String username;
    private final String password;

    private final Map<String, Friend> friends; // <friend username, Friend>
    private final Map<String, Group> groups; // <groupName, Group>

    private final Notification notifications;

}