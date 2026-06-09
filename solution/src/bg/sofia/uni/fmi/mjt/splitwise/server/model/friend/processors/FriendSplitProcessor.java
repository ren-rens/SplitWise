package bg.sofia.uni.fmi.mjt.splitwise.server.model.friend.processors;

import bg.sofia.uni.fmi.mjt.splitwise.server.model.payments.debt.Debt;
import bg.sofia.uni.fmi.mjt.splitwise.server.model.friend.FriendConstants;

import java.util.List;

public class FriendSplitProcessor {

    /**
     * if the amount of depth is less than zero - > means the friend is in debt
     * if the amount is more than zero -> means the friend is waiting for repaying
    **/
    public static void splitDebt(List<Debt> debts, double amount, String reasonForPayment) {
        double remainingDebt = FriendDebtProcessor.cycleThroughDebts(debts, amount);

        if (remainingDebt == FriendConstants.ZERO_AMOUNT_OF_DEPT_CONSTANT) {
            return;
        }

        debts.add(new Debt(remainingDebt, reasonForPayment));
    }

}
