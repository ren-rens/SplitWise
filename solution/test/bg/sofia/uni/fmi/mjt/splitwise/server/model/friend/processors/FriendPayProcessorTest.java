package bg.sofia.uni.fmi.mjt.splitwise.server.model.friend.processors;

import bg.sofia.uni.fmi.mjt.splitwise.server.model.payments.debt.Debt;
import bg.sofia.uni.fmi.mjt.splitwise.server.model.payments.debt.DebtConstants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FriendPayProcessorTest {

    @BeforeEach
    void setUp() {
        this.debts = new ArrayList<>();
    }

    @Test
    void testPayDebtWithEmptyList() {
        FriendPayProcessor.payDebt(this.debts, AMOUNT);

        assertTrue(this.debts.isEmpty(),
            "When testing pay debt with empty list of debts" +
                "should do nothing and exit the paying");
    }

    @Test
    void testPayDebtWhenDebtGoThroughCycleThroughDebts() {
        this.debts.add(new Debt(-AMOUNT * DebtConstants.DIVISION_OF_DEBT_BETWEEN_TWO_FRIENDS,
            "reason1"));

        FriendPayProcessor.payDebt(this.debts, AMOUNT);

        assertEquals(-AMOUNT,
            this.debts.getFirst().getAmountOfDebt(),
            DebtConstants.EPSILON_OF_DEBT_SUMMARY,
            "When testing pay debt with debt that can be payed " +
                "the amount of it must be removed from the debt list of the friend of the user");
    }

    @Test
    void testPayDebtWithExactAmountOfDebt() {
        this.debts.add(new Debt(AMOUNT * DebtConstants.DEBT_MULTIPLIER,
            "reason2"));

        FriendPayProcessor.payDebt(this.debts, AMOUNT);

        assertTrue(this.debts.isEmpty(),
            "When paying the debt of a friend with the exact amount of the needed one" +
                "must remove it from the list of debts");
    }

    private List<Debt> debts;
    private static final double AMOUNT = 50.00;

}
