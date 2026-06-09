package bg.sofia.uni.fmi.mjt.splitwise.server.model.user.processors.status;

import bg.sofia.uni.fmi.mjt.splitwise.server.model.friend.Friend;
import bg.sofia.uni.fmi.mjt.splitwise.server.model.group.Group;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class UserStatusProcessorTest {

    @BeforeEach
    void setUp() {
        this.friends = new HashMap<>();
        this.groups = new HashMap<>();
    }

    @Test
    void testGetStatusWithFriendsDataAndGroupsData() {
        this.friends.put(FRIEND_USERNAME, this.friend);
        this.groups.put(GROUP_NAME, new Group("user", GROUP_NAME, Map.of(FRIEND_USERNAME, this.friend)));

        String result = UserStatusProcessor.getStatus(this.friends, this.groups);

        assertTrue(result.contains(FRIEND_USERNAME) && result.contains(GROUP_NAME),
            "When testing get user status with friends and group data" +
                "should print them");
    }

    @Test
    void testGetStatusWithNoFriendsDataAndNoGroupsData() {
        String result = UserStatusProcessor.getStatus(this.friends, this.groups);

        assertTrue(!result.contains(FRIEND_USERNAME) && !result.contains(GROUP_NAME),
            "When testing get user status with NO friends and NO group data" +
                "should print that there are no friends nor groups");
    }

    private Map<String, Friend> friends;
    public Map<String, Group> groups;

    private static final String FRIEND_USERNAME = "friendUsername";
    private static final String GROUP_NAME = "groupName";
    private static final double AMOUNT = 10.00;
    private static final String REASON = "reason for payment";


    private final Friend friend = new Friend(FRIEND_USERNAME, AMOUNT, REASON);

}
