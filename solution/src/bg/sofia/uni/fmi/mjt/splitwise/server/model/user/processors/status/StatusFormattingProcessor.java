package bg.sofia.uni.fmi.mjt.splitwise.server.model.user.processors.status;

import bg.sofia.uni.fmi.mjt.splitwise.server.model.group.Group;
import bg.sofia.uni.fmi.mjt.splitwise.server.model.friend.Friend;
import bg.sofia.uni.fmi.mjt.splitwise.server.model.payments.debt.Debt;
import bg.sofia.uni.fmi.mjt.splitwise.server.model.payments.debt.DebtConstants;

import java.util.Map;

public class StatusFormattingProcessor {

    public static String getStatusForFriendsDept(Map<String, Friend> friends) {
        StringBuilder result = new StringBuilder();

        for (Map.Entry<String, Friend> entry : friends.entrySet()) {
            result.append("Friend: " + entry.getKey());
            result.append(System.lineSeparator());

            String currFriendMessage = getFriendDebtMessage(entry.getValue(), entry.getKey());
            if (currFriendMessage == null) {
                continue;
            }

            result.append(currFriendMessage);
            result.append(System.lineSeparator());
        }

        if (result.isEmpty()) {
            return "No friends debt" + System.lineSeparator();
        }

        return result.toString();
    }

    public static String getStatusForGroupsDept(Map<String, Group> groups) {
        StringBuffer result = new StringBuffer();

        for (Map.Entry<String, Group> entry : groups.entrySet()) {
            result.append("Group: " + entry.getKey());
            result.append(System.lineSeparator());

            Group currGroup = entry.getValue();

            Map<String, Friend> currGroupFriends = currGroup.getFriends();
            result.append(getStatusForFriendsOfGroup(currGroupFriends));
        }

        if (result.isEmpty()) {
            return "No groups debt" + System.lineSeparator();
        }

        return result.toString();
    }

    private static String getStatusForFriendsOfGroup(Map<String, Friend> currFriends) {
        StringBuilder friendsResult = new StringBuilder();

        for (Map.Entry<String, Friend> entry : currFriends.entrySet()) {
            String friendUsername = entry.getKey();
            Friend friend = entry.getValue();

            String currFriendMessage = getFriendDebtMessage(friend, friendUsername);
            if (currFriendMessage == null) {
                continue;
            }

            friendsResult.append(currFriendMessage);
            friendsResult.append(System.lineSeparator());
        }

        return friendsResult.toString();
    }

    private static String getFriendDebtMessage(Friend currFriend, String friendName) {

        StringBuilder debtMessages = new StringBuilder();
        for (Debt debt : currFriend.getDebts()) {
            double amountOfDept = debt.getAmountOfDebt();

            if (amountOfDept < DebtConstants.ZERO_DEBT) {
                debtMessages.append(friendName + ": Owes you " + Math.abs(amountOfDept) +
                    " [ " + debt.getReasonForPayment() + " ] ");
            } else if (amountOfDept > DebtConstants.ZERO_DEBT) {
                debtMessages.append(friendName + ": You owe " + amountOfDept  +
                    " [ " + debt.getReasonForPayment() + " ] ");
            }

            debtMessages.append(System.lineSeparator());
        }

        return debtMessages.isEmpty() ? null : debtMessages.toString();
    }

}
