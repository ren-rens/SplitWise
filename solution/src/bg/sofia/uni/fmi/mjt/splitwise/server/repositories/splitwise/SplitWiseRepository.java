package bg.sofia.uni.fmi.mjt.splitwise.server.repositories.splitwise;

import bg.sofia.uni.fmi.mjt.splitwise.exceptions.GroupNamesMustContainAtLeastOneNonBlankSymbolException;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.GroupWithThisNameDoesNotExistException;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.NoFriendWithSuchUsernameExistsException;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.UserCannotAddSelfAsFriend;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.UserDataMustContainAtLeastOneNonBlankSymbolException;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.UserWIthThisUsernameDoesNotHaveAnyPaymentHistory;
import bg.sofia.uni.fmi.mjt.splitwise.server.Session;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.GroupWithThisNameAlreadyExistsException;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.IncorrectPasswordForUserNameException;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.NotEnoughUsersToCreateAGroupException;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.UserAlreadyHasFriendWithSuchUsernameException;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.UserWithThisUserNameExistsException;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.UserWithThisUsernameDoesNotExistException;
import bg.sofia.uni.fmi.mjt.splitwise.server.model.requests.UserSplitRequest;
import bg.sofia.uni.fmi.mjt.splitwise.server.model.user.User;
import bg.sofia.uni.fmi.mjt.splitwise.server.repositories.splitwise.processors.LoginProcessor;
import bg.sofia.uni.fmi.mjt.splitwise.server.repositories.splitwise.processors.RegisterProcessor;
import bg.sofia.uni.fmi.mjt.splitwise.server.repositories.splitwise.processors.data.UserDataGettingProcessor;
import bg.sofia.uni.fmi.mjt.splitwise.server.repositories.user.UserRepository;
import bg.sofia.uni.fmi.mjt.splitwise.server.repositories.splitwise.processors.FriendAddingProcessor;
import bg.sofia.uni.fmi.mjt.splitwise.server.repositories.splitwise.processors.GroupCreationProcessor;
import bg.sofia.uni.fmi.mjt.splitwise.server.repositories.splitwise.processors.payments.PaymentProcessor;
import bg.sofia.uni.fmi.mjt.splitwise.server.repositories.splitwise.processors.payments.SplitProcessor;

public class SplitWiseRepository {

    public SplitWiseRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void registerUser(String username, String password)
        throws UserWithThisUserNameExistsException, UserDataMustContainAtLeastOneNonBlankSymbolException {
        RegisterProcessor.registerUser(this.userRepository, username, password);
    }

    public String loginUser(String username, String password, Session session) throws
        UserWithThisUsernameDoesNotExistException, IncorrectPasswordForUserNameException {
        return LoginProcessor.loginUser(this.userRepository, username, password, session);
    }

    public void addFriend(String loggedUsername, String friendUsername)
        throws UserWithThisUsernameDoesNotExistException, UserAlreadyHasFriendWithSuchUsernameException,
        UserCannotAddSelfAsFriend, UserDataMustContainAtLeastOneNonBlankSymbolException {
        FriendAddingProcessor.addFriend(this.userRepository, loggedUsername, friendUsername);
    }

    public void createGroup(String loggedUsername, String groupName, String[] friendsUsernames)
        throws UserWithThisUsernameDoesNotExistException, NotEnoughUsersToCreateAGroupException,
        GroupWithThisNameAlreadyExistsException, GroupNamesMustContainAtLeastOneNonBlankSymbolException {
        GroupCreationProcessor.createGroup(this.userRepository, loggedUsername, groupName, friendsUsernames);
    }

    public void split(UserSplitRequest request)
        throws NoFriendWithSuchUsernameExistsException, GroupWithThisNameDoesNotExistException,
        UserWithThisUsernameDoesNotExistException {
        SplitProcessor.split(this.userRepository, request);
    }

    public String getStatus(String loggedUsername) throws UserWithThisUsernameDoesNotExistException {
        User loggedUser = this.userRepository.findUserByUsername(loggedUsername);
        SplitWiseValidator.validateUserExists(loggedUser);

        return loggedUser.getStatus();
    }

    public void payed(String loggedUsername, String groupName, double amount, String friendUsername)
        throws NoFriendWithSuchUsernameExistsException, GroupWithThisNameDoesNotExistException,
        UserWithThisUsernameDoesNotExistException {
        PaymentProcessor.payed(this.userRepository, loggedUsername, groupName, amount, friendUsername);
    }

    public String checkHistory(String userName)
        throws UserWithThisUsernameDoesNotExistException, UserWIthThisUsernameDoesNotHaveAnyPaymentHistory {
        SplitWiseValidator.validateUserExistenceByUsername(this.userRepository, userName);

        return this.userRepository.getFormattedPaymentHistoryByUsername(userName);
    }

    public String getFriendsOfUser(String loggedUsername) throws UserWithThisUsernameDoesNotExistException {
        return UserDataGettingProcessor.getFriendsOfUser(this.userRepository, loggedUsername);
    }

    public String getGroupOfUser(String loggedUsername) throws UserWithThisUsernameDoesNotExistException {
        return UserDataGettingProcessor.getGroupsOfUser(this.userRepository, loggedUsername);
    }

    private final UserRepository userRepository;

}