package bg.sofia.uni.fmi.mjt.splitwise.server.repositories.splitwise.processors.data;

import bg.sofia.uni.fmi.mjt.splitwise.server.model.group.Group;
import bg.sofia.uni.fmi.mjt.splitwise.server.model.friend.Friend;

import java.util.Map;

public class UserDataFormattingProcessor {

    public static String formatGroupsOfUser(Map<String, Group> groups) {
        StringBuilder builder = new StringBuilder();
        for (Map.Entry<String, Group> entry : groups.entrySet()) {
            String groupName = entry.getKey();
            Group group = entry.getValue();

            Map<String, Friend> friends = group.getFriends();
            String friendsNames = formatFriendsOfUser(friends);

            builder.append("Group: " + groupName + System.lineSeparator()
                + "Friends in group: " + System.lineSeparator() +
                friendsNames);
        }

        return builder.toString();
    }

    public static String formatFriendsOfUser(Map<String, Friend> friends) {
        StringBuilder builder = new StringBuilder();
        for (Map.Entry<String, Friend> entry : friends.entrySet()) {
            builder.append("Friend: " + entry.getKey());
            builder.append(System.lineSeparator());
        }

        return builder.toString();
    }

}
