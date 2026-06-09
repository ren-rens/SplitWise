package bg.sofia.uni.fmi.mjt.splitwise.server.model.payments;

import bg.sofia.uni.fmi.mjt.splitwise.exceptions.UserWIthThisUsernameDoesNotHaveAnyPaymentHistory;
import bg.sofia.uni.fmi.mjt.splitwise.server.model.payments.debt.Debt;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class PaymentHistory implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    public PaymentHistory(String splitterOfDebt, Map<String, List<Debt>> payments) {
        this.splitterOfDebt = splitterOfDebt;
        this.payments = payments;
        // this.payments = payments;
    }

    public void updatePayments(List<Debt> debts, String splitterOfDebt) {
        if (!this.payments.containsKey(splitterOfDebt)) {
            this.payments.put(splitterOfDebt, debts);
            return;
        }

        this.payments.replace(splitterOfDebt, debts);
    }

    public String getSplitterOfDebt() {
        return this.splitterOfDebt;
    }

    public Map<String, List<Debt>> getPayments() {
        return this.payments;
    }

    public String paymentsFormatter() throws UserWIthThisUsernameDoesNotHaveAnyPaymentHistory {
        StringBuffer buffer = new StringBuffer();

        for (Map.Entry<String, List<Debt>> entry : this.payments.entrySet()) {
            String targetId = entry.getKey();
            List<Debt> debts = entry.getValue();

            paymentsFormatterForCurrentFriendDebts(buffer, debts, targetId);
        }

        if (buffer.isEmpty()) {
            throw new UserWIthThisUsernameDoesNotHaveAnyPaymentHistory(
                "No payment information from this user was saved");
        }

        return buffer.toString();
    }

    private void paymentsFormatterForCurrentFriendDebts(StringBuffer buffer, List<Debt> debts, String targetId) {
        for (Debt debt : debts) {
                /**
                 * because we save in the file only the payments the user has made
                 * that means all the saved debts will be of friends/groups that own the user money
                 **/
            buffer.append(String.format("You split %.2f each with %s [%s]",
                Math.abs(debt.getAmountOfDebt()), targetId, debt.getReasonForPayment()));
            buffer.append(System.lineSeparator());
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        PaymentHistory that = (PaymentHistory) o;
        return Objects.equals(splitterOfDebt, that.splitterOfDebt);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(splitterOfDebt);
    }

    private final String splitterOfDebt;
    private final Map<String, List<Debt>> payments; // <friend the user split with/group, Friend with debts>

}
