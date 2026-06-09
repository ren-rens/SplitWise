package bg.sofia.uni.fmi.mjt.splitwise.server.repositories.splitwise.processors.payments;

import bg.sofia.uni.fmi.mjt.splitwise.exceptions.GroupWithThisNameDoesNotExistException;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.NoFriendWithSuchUsernameExistsException;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.UserWithThisUsernameDoesNotExistException;
import bg.sofia.uni.fmi.mjt.splitwise.server.model.requests.FriendSplitRequest;
import bg.sofia.uni.fmi.mjt.splitwise.server.model.group.Group;
import bg.sofia.uni.fmi.mjt.splitwise.server.model.requests.UserSplitRequest;
import bg.sofia.uni.fmi.mjt.splitwise.server.model.payments.debt.DebtConstants;
import bg.sofia.uni.fmi.mjt.splitwise.server.model.friend.Friend;
import bg.sofia.uni.fmi.mjt.splitwise.server.model.user.User;
import bg.sofia.uni.fmi.mjt.splitwise.server.model.user.UserValidator;
import bg.sofia.uni.fmi.mjt.splitwise.server.repositories.user.UserRepository;
import bg.sofia.uni.fmi.mjt.splitwise.server.repositories.splitwise.SplitWiseValidator;

import java.util.Map;

public class SplitProcessor {

    public static void split(UserRepository userRepository, UserSplitRequest request)
        throws UserWithThisUsernameDoesNotExistException, NoFriendWithSuchUsernameExistsException,
        GroupWithThisNameDoesNotExistException {
        User loggedUser = userRepository.findUserByUsername(request.loggedUsername());
        SplitWiseValidator.validateUserExists(loggedUser);

        if (!request.isGroup()) {
            splitBetweenFriends(loggedUser, request, userRepository);
        } else {
            splitBetweenFriendsOfGroup(loggedUser, request, userRepository);
        }
    }

    private static void splitBetweenFriends(User loggedUser, UserSplitRequest request,
                                           UserRepository userRepository)
        throws NoFriendWithSuchUsernameExistsException {
        double divisionOfDebt = getDivisionOfDebtBetweenFriends();
        double amount = request.amount() / divisionOfDebt;

        loggedUser.splitWithFriend(amount * DebtConstants.DEBT_MULTIPLIER,
            request.targetId(), request.reasonForPayment());

        userRepository.updateUsers(loggedUser);
        userRepository.updatePaymentHistory(loggedUser, request.targetId());

        FriendSplitRequest friendRequest = new FriendSplitRequest(request.targetId(), null,
            amount, request.loggedUsername(), request.reasonForPayment());
        updateFriendSplitting(userRepository, friendRequest);
    }

    private static void splitBetweenFriendsOfGroup(User loggedUser, UserSplitRequest request,
                                                   UserRepository userRepository)
        throws GroupWithThisNameDoesNotExistException, NoFriendWithSuchUsernameExistsException {

        double divisionOfDebt = getDivisionOfDebtBetweenGroup(loggedUser, request.targetId());
        double amount = request.amount() / divisionOfDebt;

        loggedUser.splitWithGroup(amount * DebtConstants.DEBT_MULTIPLIER,
            request.targetId(), request.reasonForPayment());

        userRepository.updateUsers(loggedUser);
        userRepository.updatePaymentHistory(loggedUser, request.targetId());

        updateGroupSplitting(userRepository, loggedUser, request, amount);
    }

    private static void updateGroupSplitting(UserRepository userRepository, User loggedUser,
                                      UserSplitRequest request, double amount)
        throws NoFriendWithSuchUsernameExistsException {
        Group group = loggedUser.getGroups().get(request.targetId());
        Map<String, Friend> friends = group.getFriends();

        for (Map.Entry<String, Friend> entry : friends.entrySet()) {
            String friendUsername = entry.getKey();

            // update the friend in the data of the other user
            FriendSplitRequest friendRequest = new FriendSplitRequest(friendUsername, request.targetId(),
                amount, request.loggedUsername(), request.reasonForPayment());
            updateFriendSplitting(userRepository, friendRequest);
        }
    }

    private static double getDivisionOfDebtBetweenFriends() {
        return DebtConstants.DIVISION_OF_DEBT_BETWEEN_TWO_FRIENDS;
    }

    private static double getDivisionOfDebtBetweenGroup(User loggedUser, String target)
        throws GroupWithThisNameDoesNotExistException {
        UserValidator.validateAvailabilityOfUserGroups(loggedUser.getGroups(), target);

        Group group = loggedUser.getGroups().get(target);
        Map<String, Friend> friends = group.getFriends();

        return friends.size() + DebtConstants.USER_GROUP_MEMBER_COUNT;
    }

    private static void updateFriendSplitting(UserRepository userRepository,
                                              FriendSplitRequest friendRequest)
        throws NoFriendWithSuchUsernameExistsException {
        User friend = userRepository.findUserByUsername(friendRequest.friendUsername());

        if (friendRequest.group() == null) {
            friend.splitWithFriend(friendRequest.amount(), friendRequest.username(),
                friendRequest.reasonForPayment());
        } else {
            friend.splitWithFriendInGroup(friendRequest.group(), friendRequest.username(),
                friendRequest.amount(), friendRequest.reasonForPayment());
        }

        NotificationsProcessor.updateNotificationsWhenSplitting(friend, friendRequest.group(),
            friendRequest.amount(), friendRequest.username(), friendRequest.reasonForPayment());
        userRepository.updateUsers(friend);
    }

}
