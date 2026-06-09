package bg.sofia.uni.fmi.mjt.splitwise.server.repositories.splitwise.processors.data;

import bg.sofia.uni.fmi.mjt.splitwise.exceptions.UserWithThisUsernameDoesNotExistException;
import bg.sofia.uni.fmi.mjt.splitwise.server.model.group.Group;
import bg.sofia.uni.fmi.mjt.splitwise.server.model.friend.Friend;
import bg.sofia.uni.fmi.mjt.splitwise.server.model.user.User;
import bg.sofia.uni.fmi.mjt.splitwise.server.repositories.splitwise.SplitWiseValidator;
import bg.sofia.uni.fmi.mjt.splitwise.server.repositories.user.UserRepository;

import java.util.Map;

public class UserDataGettingProcessor {

    public static String getFriendsOfUser(UserRepository userRepository, String loggedUsername)
        throws UserWithThisUsernameDoesNotExistException {
        User loggedUser = getValidUserFromUsername(userRepository, loggedUsername);

        Map<String, Friend> friends = loggedUser.getFriends();
        return UserDataFormattingProcessor.formatFriendsOfUser(friends);
    }

    public static String getGroupsOfUser(UserRepository userRepository, String loggedUsername)
        throws UserWithThisUsernameDoesNotExistException {
        User loggedUser = getValidUserFromUsername(userRepository, loggedUsername);

        Map<String, Group> groups = loggedUser.getGroups();
        return UserDataFormattingProcessor.formatGroupsOfUser(groups);
    }

    private static User getValidUserFromUsername(UserRepository userRepository, String loggedUsername)
        throws UserWithThisUsernameDoesNotExistException {
        User loggedUser = userRepository.findUserByUsername(loggedUsername);
        SplitWiseValidator.validateUserExists(loggedUser);

        return loggedUser;
    }

}
