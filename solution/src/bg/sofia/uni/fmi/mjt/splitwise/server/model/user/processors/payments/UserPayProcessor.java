package bg.sofia.uni.fmi.mjt.splitwise.server.model.user.processors.payments;

import bg.sofia.uni.fmi.mjt.splitwise.exceptions.GroupWithThisNameDoesNotExistException;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.NoFriendWithSuchUsernameExistsException;
import bg.sofia.uni.fmi.mjt.splitwise.server.model.group.Group;
import bg.sofia.uni.fmi.mjt.splitwise.server.model.friend.Friend;
import bg.sofia.uni.fmi.mjt.splitwise.server.model.user.UserValidator;

import java.util.Map;

public class UserPayProcessor {

    public static void payFriend(Map<String, Friend> friends,
                                 String friendUsername,
                                 double amount) throws NoFriendWithSuchUsernameExistsException {
        UserValidator.validateAvailabilityOfUserFriends(friends, friendUsername);

        Friend friend = friends.get(friendUsername);
        friend.payDebt(amount);
    }

    public static void payFriendFromGroup(Map<String, Group> groups,
                                          String groupName,
                                          String friendUsername,
                                          double amount)
        throws GroupWithThisNameDoesNotExistException, NoFriendWithSuchUsernameExistsException {
        UserValidator.validateAvailabilityOfUserGroups(groups, groupName);

        Group group = groups.get(groupName);
        Friend friend = group.getFriendFromUserName(friendUsername);

        friend.payDebt(amount);
        group.updateFriendByUsername(friendUsername, friend);
    }

}
