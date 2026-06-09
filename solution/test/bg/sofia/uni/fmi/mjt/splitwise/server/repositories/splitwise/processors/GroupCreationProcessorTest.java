package bg.sofia.uni.fmi.mjt.splitwise.server.repositories.splitwise.processors;

import bg.sofia.uni.fmi.mjt.splitwise.exceptions.GroupNamesMustContainAtLeastOneNonBlankSymbolException;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.GroupWithThisNameAlreadyExistsException;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.NotEnoughUsersToCreateAGroupException;
import bg.sofia.uni.fmi.mjt.splitwise.server.model.user.User;
import bg.sofia.uni.fmi.mjt.splitwise.server.repositories.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class GroupCreationProcessorTest {

    @BeforeEach
    void setUp() {
        this.mockUserRepository = mock(UserRepository.class);
    }

    @Test
    void testCreateGroupSuccessfully() throws Exception {
        when(this.mockUserRepository.findUserByUsername(LOGGED_USERNAME)).thenReturn(LOGGED_USER);
        when(this.mockUserRepository.findUserByUsername(FRIEND_ONE_USERNAME)).thenReturn(FRIEND_ONE);
        when(this.mockUserRepository.findUserByUsername(FRIEND_TWO_USERNAME)).thenReturn(FRIEND_TWO);
        when(this.mockUserRepository.doesUserExist(FRIEND_ONE_USERNAME)).thenReturn(true);
        when(this.mockUserRepository.doesUserExist(FRIEND_TWO_USERNAME)).thenReturn(true);

        String[] friends = {FRIEND_ONE_USERNAME, FRIEND_TWO_USERNAME};

        GroupCreationProcessor.createGroup(this.mockUserRepository, LOGGED_USERNAME, GROUP_NAME, friends);

        assertTrue(LOGGED_USER.getGroups().containsKey(GROUP_NAME),
            "After successfully creating a group" +
                "logged user should have the group");
        assertTrue(FRIEND_ONE.getGroups().containsKey(GROUP_NAME),
            "After successfully creating a group" +
            "Friend1 should have the group");
        assertTrue(FRIEND_TWO.getGroups().containsKey(GROUP_NAME),
            "After successfully creating a group" +
            "Friend2 should have the group");

        verify(this.mockUserRepository, times(3)).updateUsers(any(User.class));
        verify(this.mockUserRepository).updateUsers(LOGGED_USER);
        verify(this.mockUserRepository).updateUsers(FRIEND_ONE);
        verify(this.mockUserRepository).updateUsers(FRIEND_TWO);
    }

    @Test
    void testCreateGroupWithBlankGroupName() {
        String[] friends = {FRIEND_ONE_USERNAME};

        assertThrows(GroupNamesMustContainAtLeastOneNonBlankSymbolException.class, () ->
                GroupCreationProcessor.createGroup(this.mockUserRepository, LOGGED_USERNAME, "   ", friends),
            "When testing create group with blank groupName" +
                "should throw exception for blank group name"
        );

        verify(this.mockUserRepository, never()).findUserByUsername(anyString());
        verify(this.mockUserRepository, never()).updateUsers(any(User.class));
    }

    @Test
    void testCreateGroupWithNullGroupName() {
        String[] friends = {FRIEND_ONE_USERNAME, FRIEND_TWO_USERNAME};

        assertThrows(GroupNamesMustContainAtLeastOneNonBlankSymbolException.class, () ->
                GroupCreationProcessor.createGroup(this.mockUserRepository, LOGGED_USERNAME, null, friends),
            "When testing create group with null groupName" +
                "Should throw exception for null group name"
        );

        verify(this.mockUserRepository, never()).findUserByUsername(anyString());
        verify(this.mockUserRepository, never()).updateUsers(any(User.class));
    }

    @Test
    void testCreateGroupWithDuplicateGroupName() throws GroupWithThisNameAlreadyExistsException {
        LOGGED_USER.addGroup("existingGroup", Set.of(FRIEND_ONE_USERNAME, FRIEND_TWO_USERNAME));

        when(this.mockUserRepository.findUserByUsername(LOGGED_USERNAME)).thenReturn(LOGGED_USER);

        when(this.mockUserRepository.doesUserExist(FRIEND_ONE_USERNAME)).thenReturn(true);
        when(this.mockUserRepository.findUserByUsername(FRIEND_ONE_USERNAME)).thenReturn(FRIEND_ONE);


        when(this.mockUserRepository.doesUserExist(FRIEND_TWO_USERNAME)).thenReturn(true);
        when(this.mockUserRepository.findUserByUsername(FRIEND_TWO_USERNAME)).thenReturn(FRIEND_TWO);

        String[] friends = {FRIEND_ONE_USERNAME, FRIEND_TWO_USERNAME};

        assertThrows(GroupWithThisNameAlreadyExistsException.class, () ->
                GroupCreationProcessor.createGroup(this.mockUserRepository, LOGGED_USERNAME, "existingGroup", friends),
            "When testing create group with a groupName that the user already has saved in his repository" +
                "should throw exception saying group name already exists for the user"
        );

        verify(this.mockUserRepository, never()).updateUsers(any(User.class));
    }

    @Test
    void testCreateGroupWithNoValidUsers() {
        when(this.mockUserRepository.findUserByUsername(LOGGED_USERNAME)).thenReturn(LOGGED_USER);
        when(this.mockUserRepository.doesUserExist("nonExistent1")).thenReturn(false);
        when(this.mockUserRepository.doesUserExist("nonExistent2")).thenReturn(false);

        String[] friends = {"nonExistent1", "nonExistent2"};

        assertThrows(NotEnoughUsersToCreateAGroupException.class, () ->
                GroupCreationProcessor.createGroup(this.mockUserRepository, LOGGED_USERNAME, GROUP_NAME, friends),
            "When testing create group with no valid users to add to group" +
                " Should throw exception saying it is impossible"
        );

        verify(this.mockUserRepository, never()).updateUsers(any(User.class));
    }

    private UserRepository mockUserRepository;
    private static final String LOGGED_USERNAME = "loggedUser";
    private static final String FRIEND_ONE_USERNAME = "friend1";
    private static final String FRIEND_TWO_USERNAME = "friend2";
    private static final String PASSWORD = "password";
    private static final String GROUP_NAME = "groupName";

    private static final User LOGGED_USER = new User(LOGGED_USERNAME, PASSWORD);
    private static final User FRIEND_ONE = new User(FRIEND_ONE_USERNAME, PASSWORD);
    private static final User FRIEND_TWO = new User(FRIEND_TWO_USERNAME, PASSWORD);

}