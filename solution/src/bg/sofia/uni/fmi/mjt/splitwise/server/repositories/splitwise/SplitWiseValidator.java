package bg.sofia.uni.fmi.mjt.splitwise.server.repositories.splitwise;

import bg.sofia.uni.fmi.mjt.splitwise.commands.constants.ExecutorConstants;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.GroupNamesMustContainAtLeastOneNonBlankSymbolException;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.IncorrectPasswordForUserNameException;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.NotEnoughUsersToCreateAGroupException;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.UserCannotAddSelfAsFriend;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.UserDataMustContainAtLeastOneNonBlankSymbolException;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.UserWithThisUserNameExistsException;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.UserWithThisUsernameDoesNotExistException;
import bg.sofia.uni.fmi.mjt.splitwise.server.model.user.User;
import bg.sofia.uni.fmi.mjt.splitwise.server.repositories.user.UserRepository;

public class SplitWiseValidator {

    public static void validateUserExists(User user)
        throws UserWithThisUsernameDoesNotExistException {
        if (user == null) {
            throw new UserWithThisUsernameDoesNotExistException("User with this username not found");
        }
    }

    public static void validateUserExistenceByUsername(UserRepository userRepository, String friend)
        throws UserWithThisUsernameDoesNotExistException {
        if (!userRepository.doesUserExist(friend)) {
            throw new UserWithThisUsernameDoesNotExistException("User with this username not found");
        }
    }

    public static void validatePasswordCorrectness(User user, String password)
        throws IncorrectPasswordForUserNameException {
        if (!user.getPassword().equals(password)) {
            throw new IncorrectPasswordForUserNameException("UserName and password inconsistency!");
        }
    }

    public static void validateUserNonExistenceByUsername(UserRepository userRepository, String username)
        throws UserWithThisUserNameExistsException {
        if (userRepository.doesUserExist(username)) {
            throw new UserWithThisUserNameExistsException("User should have distinctive username!");
        }
    }

    public static void validateUserDataCorrectness(String username, String password)
        throws UserDataMustContainAtLeastOneNonBlankSymbolException {
        if (username == null || username.isBlank() ||
            password == null || password.isBlank()) {
            throw new UserDataMustContainAtLeastOneNonBlankSymbolException("User names and password must be filled");
        }
    }

    public static void validateAddFriend(String username, String friend)
        throws UserCannotAddSelfAsFriend, UserDataMustContainAtLeastOneNonBlankSymbolException {
        validateUserDataCorrectness(username, friend);

        if (username.equals(friend)) {
            throw new UserCannotAddSelfAsFriend("In order to add a friend it should not be the self user");
        }
    }

    public static void validateCreateGroupUsersCount(int usersSize)
        throws NotEnoughUsersToCreateAGroupException {
        if (usersSize < ExecutorConstants.CREATE_GROUP_ARGUMENTS_COUNT) {
            throw new NotEnoughUsersToCreateAGroupException(
                "In order to create a group should give at least 4 arguments"
            );
        }
    }

    public static void validateCreateGroupGroupName(String groupName)
        throws GroupNamesMustContainAtLeastOneNonBlankSymbolException {
        if (groupName == null || groupName.isBlank()) {
            throw new GroupNamesMustContainAtLeastOneNonBlankSymbolException("Group names must be non-blank");
        }
    }

}
