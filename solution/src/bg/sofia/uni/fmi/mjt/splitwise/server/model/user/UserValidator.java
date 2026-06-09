package bg.sofia.uni.fmi.mjt.splitwise.server.model.user;

import bg.sofia.uni.fmi.mjt.splitwise.exceptions.GroupWithThisNameAlreadyExistsException;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.GroupWithThisNameDoesNotExistException;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.NoFriendWithSuchUsernameExistsException;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.UserAlreadyHasFriendWithSuchUsernameException;
import bg.sofia.uni.fmi.mjt.splitwise.server.model.group.Group;
import bg.sofia.uni.fmi.mjt.splitwise.server.model.friend.Friend;

import java.util.Map;

public class UserValidator {

    public static void validateUniquenessOfUserFriends(Map<String, Friend> friends, String username)
        throws UserAlreadyHasFriendWithSuchUsernameException {
        if (friends.containsKey(username)) {
            throw new UserAlreadyHasFriendWithSuchUsernameException("Cannot add a friend that is already your friend");
        }
    }

    public static void validateAvailabilityOfUserFriends(Map<String, Friend> friends, String username)
        throws NoFriendWithSuchUsernameExistsException {
        if (!friends.containsKey(username)) {
            throw new NoFriendWithSuchUsernameExistsException("Cannot split cash with a friend you do not have");
        }
    }

    public static void validateUniquenessOfUserGroups(Map<String, Group> groups, String groupName)
        throws GroupWithThisNameAlreadyExistsException {
        if (groups.containsKey(groupName)) {
            throw new GroupWithThisNameAlreadyExistsException("Group names should be distinctive");
        }
    }

    public static void validateAvailabilityOfUserGroups(Map<String, Group> groups, String groupName)
        throws GroupWithThisNameDoesNotExistException {
        if (!groups.containsKey(groupName)) {
            throw new GroupWithThisNameDoesNotExistException("Cannot split money with non-existent group");
        }
    }

}
