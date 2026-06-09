package bg.sofia.uni.fmi.mjt.splitwise.server.model.user.processors.payments;

import bg.sofia.uni.fmi.mjt.splitwise.exceptions.NoFriendWithSuchUsernameExistsException;
import bg.sofia.uni.fmi.mjt.splitwise.server.model.group.Group;
import bg.sofia.uni.fmi.mjt.splitwise.server.model.friend.Friend;
import bg.sofia.uni.fmi.mjt.splitwise.server.model.user.UserValidator;

import java.util.Map;

public class UserSplitProcessor {

    public static void splitWithFriend(Map<String, Friend> friends,
                                       double amount, String friendUsername, String reasonForPayment)
        throws NoFriendWithSuchUsernameExistsException {
        UserValidator.validateAvailabilityOfUserFriends(friends, friendUsername);

        Friend friend = friends.get(friendUsername);
        friend.splitDebt(amount, reasonForPayment);
    }

    public static void splitWithGroup(Map<String, Group> groups,
                               double amount, String groupName, String reasonForPayment) {
        Group group = groups.get(groupName);
        Map<String, Friend> friends = group.getFriends();

        for (Map.Entry<String, Friend> entry : friends.entrySet()) {
            Friend friend = entry.getValue();
            friend.splitDebt(amount, reasonForPayment);
        }
    }

    public static void splitWithFriendInGroup(Map<String, Group> groups, String groupName,
                                              String friendUsername, double amount, String reason)
        throws NoFriendWithSuchUsernameExistsException {
        Group group = groups.get(groupName);
        Friend friend = group.getFriendFromUserName(friendUsername);

        friend.splitDebt(amount, reason);
        // this could be useless because we work with references
        group.updateFriendByUsername(friendUsername, friend);
    }

}
