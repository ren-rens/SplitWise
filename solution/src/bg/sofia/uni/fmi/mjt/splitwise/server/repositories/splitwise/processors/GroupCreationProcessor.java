package bg.sofia.uni.fmi.mjt.splitwise.server.repositories.splitwise.processors;

import bg.sofia.uni.fmi.mjt.splitwise.exceptions.GroupNamesMustContainAtLeastOneNonBlankSymbolException;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.GroupWithThisNameAlreadyExistsException;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.NotEnoughUsersToCreateAGroupException;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.UserWithThisUsernameDoesNotExistException;
import bg.sofia.uni.fmi.mjt.splitwise.server.model.user.User;
import bg.sofia.uni.fmi.mjt.splitwise.server.repositories.user.UserRepository;
import bg.sofia.uni.fmi.mjt.splitwise.server.repositories.splitwise.SplitWiseValidator;

import java.util.HashSet;
import java.util.Set;

public class GroupCreationProcessor {

    public static void createGroup(UserRepository userRepository, String loggedUsername,
                                   String groupName, String[] friendsUsernames)
        throws GroupWithThisNameAlreadyExistsException, NotEnoughUsersToCreateAGroupException,
        UserWithThisUsernameDoesNotExistException, GroupNamesMustContainAtLeastOneNonBlankSymbolException {
        SplitWiseValidator.validateCreateGroupGroupName(groupName);

        User loggedUser = userRepository.findUserByUsername(loggedUsername);

        Set<String> friends = getAllExistingUsers(userRepository, friendsUsernames);
        SplitWiseValidator.validateCreateGroupUsersCount(friends.size());

        addGroupToUser(userRepository, loggedUser, groupName, friends);
        addGroupToAllFriends(userRepository, friends, loggedUsername, groupName);
    }

    private static void addGroupToUser(UserRepository userRepository, User user,
                                       String groupName, Set<String> friends)
        throws GroupWithThisNameAlreadyExistsException,
        UserWithThisUsernameDoesNotExistException {
        SplitWiseValidator.validateUserExists(user);

        user.addGroup(groupName, friends);
        userRepository.updateUsers(user);
    }

    private static void addGroupToAllFriends(UserRepository userRepository, Set<String> friends,
                                             String loggedUsername, String groupName)
        throws UserWithThisUsernameDoesNotExistException,
        GroupWithThisNameAlreadyExistsException {
        Set<String> currFriends = new HashSet<>(friends);
        currFriends.add(loggedUsername);

        for (String currFriendUsername : friends) {
            currFriends.remove(currFriendUsername);

            User currFriend = userRepository.findUserByUsername(currFriendUsername);
            addGroupToUser(userRepository, currFriend, groupName, currFriends);

            currFriends.add(currFriendUsername);
        }
    }

    private static Set<String> getAllExistingUsers(UserRepository userRepository, String[] usernames) {
        Set<String> users = new HashSet<>();
        for (String username : usernames) {
            if (!userRepository.doesUserExist(username)) {
                continue;
            }

            users.add(username);
        }

        return users;
    }

}
