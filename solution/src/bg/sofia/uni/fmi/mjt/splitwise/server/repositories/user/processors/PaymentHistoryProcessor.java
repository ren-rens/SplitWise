package bg.sofia.uni.fmi.mjt.splitwise.server.repositories.user.processors;

import bg.sofia.uni.fmi.mjt.splitwise.exceptions.UserWIthThisUsernameDoesNotHaveAnyPaymentHistory;
import bg.sofia.uni.fmi.mjt.splitwise.server.model.group.Group;
import bg.sofia.uni.fmi.mjt.splitwise.server.model.payments.debt.Debt;
import bg.sofia.uni.fmi.mjt.splitwise.server.model.payments.PaymentHistory;
import bg.sofia.uni.fmi.mjt.splitwise.server.model.friend.Friend;
import bg.sofia.uni.fmi.mjt.splitwise.server.model.payments.debt.DebtConstants;
import bg.sofia.uni.fmi.mjt.splitwise.server.model.user.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PaymentHistoryProcessor {

    public static void loadPaymentHistory(String paymentHistoryFile,
                                          Map<String, PaymentHistory> paymentHistory) {
        RepositoryProcessor.loadFromFile(paymentHistoryFile, paymentHistory);
    }

    public static void updatePaymentHistory(String paymentHistoryFile,
                                            Map<String, PaymentHistory> paymentHistory,
                                            User user, String target) {
        String username = user.getUsername();

        if (!paymentHistory.containsKey(username)) {
            Map<String, List<Debt>> map = new HashMap<>();
            map.put(target, new ArrayList<>());
            paymentHistory.put(username, new PaymentHistory(username, map));
        }

        List<Debt> updatedPayments = getPayments(user, target);
        PaymentHistory updatedHistory = paymentHistory.get(username);
        updatedHistory.updatePayments(updatedPayments, target);

        RepositoryProcessor.updateInFile(paymentHistoryFile, paymentHistory, username, updatedHistory);
    }

    private static List<Debt> getPayments(User user, String target) {
        if (user.getGroups().containsKey(target)) {
            Group group = user.getGroups().get(target);
            return getPaymentsFromGroup(group);
        } else if (user.getFriends().containsKey(target)) {
            Friend friend = user.getFriends().get(target);
            return getPaymentsFromFriend(friend.getDebts());
        }
        return new ArrayList<>();
    }

    private static List<Debt> getPaymentsFromGroup(Group group) {
        List<Debt> payments = new ArrayList<>();
        for (Friend friend : group.getFriends().values()) {
            payments.addAll(getPaymentsFromFriend(friend.getDebts()));
        }
        return payments;
    }

    private static List<Debt> getPaymentsFromFriend(List<Debt> debts) {
        List<Debt> payments = new ArrayList<>();
        for (Debt debt : debts) {
            if (debt.getAmountOfDebt() > DebtConstants.ZERO_DEBT) {
                continue;
            }

            payments.add(debt);
        }
        return payments;
    }

    public static String getFormattedPaymentHistoryByUsername(Map<String, PaymentHistory> paymentHistory,
                                                              String username)
        throws UserWIthThisUsernameDoesNotHaveAnyPaymentHistory {
        validateGetFormattedPaymentHistoryByUsername(paymentHistory, username);
        return paymentHistory.get(username).paymentsFormatter();
    }

    private static void validateGetFormattedPaymentHistoryByUsername(Map<String, PaymentHistory> paymentHistory,
                                                                     String username)
        throws UserWIthThisUsernameDoesNotHaveAnyPaymentHistory {
        if (!RepositoryProcessor.existsInMap(paymentHistory, username)) {
            throw new UserWIthThisUsernameDoesNotHaveAnyPaymentHistory(
                "No payment information from this user was saved");
        }
    }

}