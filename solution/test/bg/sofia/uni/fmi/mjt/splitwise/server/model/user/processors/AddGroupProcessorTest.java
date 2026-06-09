package bg.sofia.uni.fmi.mjt.splitwise.server.model.user.processors;

import bg.sofia.uni.fmi.mjt.splitwise.exceptions.GroupWithThisNameAlreadyExistsException;
import bg.sofia.uni.fmi.mjt.splitwise.server.model.group.Group;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AddGroupProcessorTest {

    @BeforeEach
    void setUp() {
        this.friends = new HashSet<>();
        this.groups = new HashMap<>();

        friends.add(FRIEND_ONE);
        friends.add(FRIEND_TWO);
    }

    @Test
    void testAddGroupSuccessfully() throws Exception {
        AddGroupProcessor.addGroup(this.groups, USERNAME, GROUP_NAME, this.friends);

        assertTrue(this.groups.containsKey(GROUP_NAME),
            "After successfully adding a group " +
                "should save this group in the data of the user");
    }

    @Test
    void testAddGroupWithSameGroupNameAsAlreadyCreatedInTheUserData() throws Exception {
        AddGroupProcessor.addGroup(this.groups, USERNAME, GROUP_NAME, this.friends);

        assertThrows(GroupWithThisNameAlreadyExistsException.class, () ->
                AddGroupProcessor.addGroup(this.groups, USERNAME, GROUP_NAME, this.friends),
            "WHen trying to add a group that was added already" +
                "should throw GroupWithThisNameAlreadyExistsException");
    }

    private static final String USERNAME = "username";
    private static final String FRIEND_ONE = "friend1";
    private static final String FRIEND_TWO = "friend2";
    private static final String GROUP_NAME = "groupName";

    private Set<String> friends;
    private Map<String, Group> groups;

}
