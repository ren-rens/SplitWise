package bg.sofia.uni.fmi.mjt.splitwise.server.model.user.processors;

import bg.sofia.uni.fmi.mjt.splitwise.exceptions.GroupWithThisNameAlreadyExistsException;
import bg.sofia.uni.fmi.mjt.splitwise.server.model.group.Group;
import bg.sofia.uni.fmi.mjt.splitwise.server.model.friend.Friend;
import bg.sofia.uni.fmi.mjt.splitwise.server.model.friend.FriendConstants;
import bg.sofia.uni.fmi.mjt.splitwise.server.model.user.UserValidator;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class AddGroupProcessor {

    public static void addGroup(Map<String, Group> groups, String username,
                         String groupName, Set<String> usernames) throws GroupWithThisNameAlreadyExistsException {
        UserValidator.validateUniquenessOfUserGroups(groups, groupName);

        Map<String, Friend> friends = getFriendsFromUsernamesToAddToGroup(usernames);
        Group group = new Group(username, groupName, friends);

        groups.put(groupName, group);
    }

    private static Map<String, Friend> getFriendsFromUsernamesToAddToGroup(Set<String> usernames) {
        Map<String, Friend> friends = new HashMap<>();

        for (String username : usernames) {
            Friend currFriend = new Friend(username, FriendConstants.ZERO_AMOUNT_OF_DEPT_CONSTANT,
                FriendConstants.REASON_FOR_PAYMENT_CONSTANT);
            friends.put(username, currFriend);
        }

        return friends;
    }

}
