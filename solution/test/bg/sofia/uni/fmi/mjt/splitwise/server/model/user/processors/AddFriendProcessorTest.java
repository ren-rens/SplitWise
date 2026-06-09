package bg.sofia.uni.fmi.mjt.splitwise.server.model.user.processors;

import bg.sofia.uni.fmi.mjt.splitwise.exceptions.UserAlreadyHasFriendWithSuchUsernameException;
import bg.sofia.uni.fmi.mjt.splitwise.server.model.friend.Friend;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AddFriendProcessorTest {

    @BeforeEach
    void setUp() {
        this.friends = new HashMap<>();
    }

    @Test
    void testAddFriendSuccessfully() throws Exception {
        AddFriendProcessor.addFriend(this.friends, USERNAME);

        assertTrue(this.friends.containsKey(USERNAME),
            "After successfully adding a friend " +
                "should save this friend in the data of the user");
    }

    @Test
    void testAddFriendWithAlreadyAddedFriend() throws Exception {
        AddFriendProcessor.addFriend(this.friends, USERNAME);

        assertThrows(UserAlreadyHasFriendWithSuchUsernameException.class, () ->
            AddFriendProcessor.addFriend(this.friends, USERNAME),
            "WHen trying to add a friend that was added already" +
                "should throw UserAlreadyHasFriendWithSuchUsernameException");
    }

    private Map<String, Friend> friends;
    private static final String USERNAME = "username";

}
