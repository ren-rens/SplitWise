package bg.sofia.uni.fmi.mjt.splitwise.server.repositories.splitwise.processors.payments;

import bg.sofia.uni.fmi.mjt.splitwise.exceptions.GroupWithThisNameDoesNotExistException;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.NoFriendWithSuchUsernameExistsException;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.UserWithThisUsernameDoesNotExistException;
import bg.sofia.uni.fmi.mjt.splitwise.server.model.user.User;
import bg.sofia.uni.fmi.mjt.splitwise.server.repositories.user.UserRepository;
import bg.sofia.uni.fmi.mjt.splitwise.server.repositories.splitwise.SplitWiseValidator;

public class PaymentProcessor {

    public static void payed(UserRepository userRepository, String loggedUsername,
                             String groupName, double amount, String username) throws
        UserWithThisUsernameDoesNotExistException, NoFriendWithSuchUsernameExistsException,
        GroupWithThisNameDoesNotExistException {

        User loggedUser = userRepository.findUserByUsername(loggedUsername);
        SplitWiseValidator.validateUserExists(loggedUser);

        if (groupName == null) {
            friendPayed(userRepository, loggedUser, amount, username);
        } else {
            friendFromGroupPayed(userRepository, loggedUser, groupName, amount, username);
        }
    }

    private static void friendPayed(UserRepository userRepository,
                                    User loggedUser,
                                    double amount,
                                    String username)
        throws NoFriendWithSuchUsernameExistsException, UserWithThisUsernameDoesNotExistException {

        User friend = userRepository.findUserByUsername(username);
        SplitWiseValidator.validateUserExists(friend);

        friend.payFriend(loggedUser.getUsername(), amount);
        loggedUser.receivePaymentFrom(friend.getUsername(), amount);

        userRepository.updateUsers(friend);
        userRepository.updateUsers(loggedUser);

        NotificationsProcessor.updateNotificationWhenPaying(friend, null, amount, loggedUser.getUsername());
        userRepository.updateUsers(friend);
    }

    private static void friendFromGroupPayed(UserRepository userRepository,
                                             User loggedUser,
                                             String groupName,
                                             double amount,
                                             String username)
        throws NoFriendWithSuchUsernameExistsException, GroupWithThisNameDoesNotExistException,
        UserWithThisUsernameDoesNotExistException {

        loggedUser.receivePaymentFromGroup(groupName, username, amount);

        User friend = userRepository.findUserByUsername(username);
        SplitWiseValidator.validateUserExists(friend);
        friend.payFriendFromGroup(groupName, loggedUser.getUsername(), amount);

        userRepository.updateUsers(loggedUser);
        userRepository.updateUsers(friend);

        NotificationsProcessor.updateNotificationWhenPaying(friend, groupName, amount, loggedUser.getUsername());
        userRepository.updateUsers(friend);
    }

}
