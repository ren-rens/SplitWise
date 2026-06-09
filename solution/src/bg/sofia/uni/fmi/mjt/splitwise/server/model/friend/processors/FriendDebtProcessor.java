package bg.sofia.uni.fmi.mjt.splitwise.server.model.friend.processors;

import bg.sofia.uni.fmi.mjt.splitwise.server.model.payments.debt.Debt;
import bg.sofia.uni.fmi.mjt.splitwise.server.model.payments.debt.DebtConstants;

import java.util.Iterator;
import java.util.List;

public class FriendDebtProcessor {

    public static double cycleThroughDebts(List<Debt> debts, double amount) {
        Iterator<Debt> debtsIterator = debts.iterator();

        while (debtsIterator.hasNext() && Math.abs(amount) > DebtConstants.EPSILON_OF_DEBT_SUMMARY) {
            Debt currDebt = debtsIterator.next();
            double currAmountDebt = currDebt.getAmountOfDebt();

            if ((currAmountDebt > DebtConstants.ZERO_DEBT && amount < DebtConstants.ZERO_DEBT)
                || (currAmountDebt < DebtConstants.ZERO_DEBT && amount > DebtConstants.ZERO_DEBT)) {
                double newAmount = currAmountDebt + amount;

                if (Math.abs(newAmount) < DebtConstants.EPSILON_OF_DEBT_SUMMARY) {
                    debtsIterator.remove();
                    amount = DebtConstants.ZERO_DEBT;
                } else if ((currAmountDebt > DebtConstants.ZERO_DEBT &&
                    newAmount > DebtConstants.ZERO_DEBT)
                    || (currAmountDebt < DebtConstants.ZERO_DEBT &&
                    newAmount < DebtConstants.ZERO_DEBT)) {
                    currDebt.updateDepth(amount);
                    amount = DebtConstants.ZERO_DEBT;
                } else {
                    debtsIterator.remove();
                    amount = newAmount;
                }
            }
        }

        return amount;
    }

}
