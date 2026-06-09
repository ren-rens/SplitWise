package bg.sofia.uni.fmi.mjt.splitwise.server.model.user.processors.status;

import bg.sofia.uni.fmi.mjt.splitwise.server.model.group.Group;
import bg.sofia.uni.fmi.mjt.splitwise.server.model.friend.Friend;

import java.util.Map;

public class UserStatusProcessor {

    public static String getStatus(Map<String, Friend> friends,
                            Map<String, Group> groups) {

        return StatusFormattingProcessor.getStatusForFriendsDept(friends) +
            StatusFormattingProcessor.getStatusForGroupsDept(groups);
    }

}
