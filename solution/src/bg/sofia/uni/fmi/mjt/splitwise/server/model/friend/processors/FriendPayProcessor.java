package bg.sofia.uni.fmi.mjt.splitwise.server.model.friend.processors;

import bg.sofia.uni.fmi.mjt.splitwise.server.model.payments.debt.Debt;
import bg.sofia.uni.fmi.mjt.splitwise.server.model.payments.debt.DebtConstants;

import java.util.List;

public class FriendPayProcessor {

    public static void payDebt(List<Debt> debts, double amount) {
        if (debts.isEmpty() || Math.abs(amount) < DebtConstants.EPSILON_OF_DEBT_SUMMARY) {
            return;
        }

        FriendDebtProcessor.cycleThroughDebts(debts, amount);
    }

}
