package bg.sofia.uni.fmi.mjt.splitwise.server.repositories.splitwise.processors;

import bg.sofia.uni.fmi.mjt.splitwise.exceptions.UserAlreadyHasFriendWithSuchUsernameException;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.UserCannotAddSelfAsFriend;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.UserDataMustContainAtLeastOneNonBlankSymbolException;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.UserWithThisUsernameDoesNotExistException;
import bg.sofia.uni.fmi.mjt.splitwise.server.model.user.User;
import bg.sofia.uni.fmi.mjt.splitwise.server.repositories.user.UserRepository;
import bg.sofia.uni.fmi.mjt.splitwise.server.repositories.splitwise.SplitWiseValidator;

public class FriendAddingProcessor {

    public static void addFriend(UserRepository userRepository,
                                 String loggedUsername, String friendUsername)
        throws UserWithThisUsernameDoesNotExistException, UserAlreadyHasFriendWithSuchUsernameException,
        UserCannotAddSelfAsFriend, UserDataMustContainAtLeastOneNonBlankSymbolException {
        SplitWiseValidator.validateAddFriend(loggedUsername, friendUsername);

        User loggedUser = userRepository.findUserByUsername(loggedUsername);
        addFriendToUser(userRepository, loggedUser, friendUsername);

        User friend = userRepository.findUserByUsername(friendUsername);
        addFriendToUser(userRepository, friend, loggedUsername);
    }

    private static void addFriendToUser(UserRepository userRepository,
                                        User user, String friendUsername)
        throws UserWithThisUsernameDoesNotExistException, UserAlreadyHasFriendWithSuchUsernameException {
        SplitWiseValidator.validateUserExists(user);
        SplitWiseValidator.validateUserExistenceByUsername(userRepository, friendUsername);

        user.addFriend(friendUsername);
        userRepository.updateUsers(user);
    }

}
