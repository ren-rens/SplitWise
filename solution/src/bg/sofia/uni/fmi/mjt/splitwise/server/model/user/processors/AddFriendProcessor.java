package bg.sofia.uni.fmi.mjt.splitwise.server.model.user.processors;

import bg.sofia.uni.fmi.mjt.splitwise.exceptions.UserAlreadyHasFriendWithSuchUsernameException;
import bg.sofia.uni.fmi.mjt.splitwise.server.model.friend.Friend;
import bg.sofia.uni.fmi.mjt.splitwise.server.model.friend.FriendConstants;
import bg.sofia.uni.fmi.mjt.splitwise.server.model.user.UserValidator;

import java.util.Map;

public class AddFriendProcessor {

    public static void addFriend(Map<String, Friend> friends, String username)
        throws UserAlreadyHasFriendWithSuchUsernameException {
        UserValidator.validateUniquenessOfUserFriends(friends, username);
        addFriendImpl(friends, username);
    }

    private static void addFriendImpl(Map<String, Friend> friends, String username) {
        Friend friend = new Friend(username, FriendConstants.ZERO_AMOUNT_OF_DEPT_CONSTANT,
            FriendConstants.REASON_FOR_PAYMENT_CONSTANT);

        friends.put(username, friend);
    }

}
