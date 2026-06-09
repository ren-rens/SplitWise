package bg.sofia.uni.fmi.mjt.splitwise.server.model.friend;

import bg.sofia.uni.fmi.mjt.splitwise.server.model.payments.debt.Debt;
import bg.sofia.uni.fmi.mjt.splitwise.server.model.friend.processors.FriendPayProcessor;
import bg.sofia.uni.fmi.mjt.splitwise.server.model.friend.processors.FriendSplitProcessor;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Friend implements Serializable {

    @Serial
    private static final long serialVersionUID = 2L;

    public Friend(String username, double amountOfDebt, String reasonForPayment) {
        this.username = username;

        this.debts = new ArrayList<>();
        if (amountOfDebt == 0) {
            return;
        }

        this.debts.add(new Debt(amountOfDebt, reasonForPayment));
    }

    public List<Debt> getDebts() {
        return this.debts;
    }

    public void splitDebt(double amount, String reasonForPayment) {
        FriendSplitProcessor.splitDebt(this.debts, amount, reasonForPayment);
    }

    public void payDebt(double amount) {
        FriendPayProcessor.payDebt(this.debts, amount);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Friend friend = (Friend) o;
        return Objects.equals(username, friend.username);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(username);
    }

    private final String username;
    private List<Debt> debts;

}
