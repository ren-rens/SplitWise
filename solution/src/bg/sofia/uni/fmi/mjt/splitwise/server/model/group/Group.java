package bg.sofia.uni.fmi.mjt.splitwise.server.model.group;

import bg.sofia.uni.fmi.mjt.splitwise.exceptions.NoFriendWithSuchUsernameExistsException;
import bg.sofia.uni.fmi.mjt.splitwise.server.model.friend.Friend;
import bg.sofia.uni.fmi.mjt.splitwise.server.model.user.UserValidator;

import java.io.Serial;
import java.io.Serializable;
import java.util.Map;
import java.util.Objects;

public class Group implements Serializable {

    @Serial
    private static final long serialVersionUID = 3L;

    public Group(String userCreator, String groupName, Map<String, Friend> friends) {
        this.userCreator = userCreator;
        this.groupName = groupName;
        this.friends = friends;
    }

    public void updateFriendByUsername(String friendUsername, Friend friend) {
        this.friends.replace(friendUsername, friend);
    }

    public Friend getFriendFromUserName(String friendUsername) throws NoFriendWithSuchUsernameExistsException {
        UserValidator.validateAvailabilityOfUserFriends(this.friends, friendUsername);

        return this.friends.get(friendUsername);
    }

    public Map<String, Friend> getFriends() {
        return this.friends;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Group group = (Group) o;
        return Objects.equals(userCreator, group.userCreator) &&
            Objects.equals(groupName, group.groupName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userCreator, groupName);
    }

    private final String userCreator;
    private final String groupName;
    private final Map<String, Friend> friends;

}
