package bg.sofia.uni.fmi.mjt.splitwise.server.model.friend.processors;

import bg.sofia.uni.fmi.mjt.splitwise.server.model.payments.debt.Debt;
import bg.sofia.uni.fmi.mjt.splitwise.server.model.payments.debt.DebtConstants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FriendSplitProcessorTest {

    @BeforeEach
    void setUp() {
        this.debts = new ArrayList<>();
    }

    @Test
    void testSplitDebtWithDebtAmountNotGettingFullyPayed() {
        this.debts.add(new Debt(AMOUNT * DebtConstants.DIVISION_OF_DEBT_BETWEEN_TWO_FRIENDS,
            REASON));

        FriendSplitProcessor.splitDebt(this.debts, -AMOUNT, REASON);

        assertEquals(AMOUNT,
            this.debts.getFirst().getAmountOfDebt(),
            DebtConstants.EPSILON_OF_DEBT_SUMMARY,
            "When testing split debt with debt that can be payed while splitting" +
                "the amount of it must be removed from the debt list of the friend of the user");
    }

    @Test
    void testSplitDebtWithExactAmountOfDebt() {
        this.debts.add(new Debt(AMOUNT * DebtConstants.DEBT_MULTIPLIER, REASON));

        FriendSplitProcessor.splitDebt(this.debts, AMOUNT, REASON);

        assertTrue(this.debts.isEmpty(),
            "When splitting the debt of a friend with the exact amount of the needed one" +
                "must remove it from the list of debts");
    }

    @Test
    void testSplitDebtWithNoWayOfRepayingWhileSplitting() {
        FriendSplitProcessor.splitDebt(this.debts, AMOUNT, REASON);

        assertFalse(this.debts.isEmpty(),
            "When splitting the debt of a friend that has no previous debts" +
                "must save it in the debts");
    }

    @Test
    void testSplitDebtWithLargeOverpayment() {
        debts.add(new Debt(AMOUNT_OVERPAYMENT, REASON));

        FriendSplitProcessor.splitDebt(debts, AMOUNT_OVERPAYMENT_SPLIT, "Overpayment");

        assertEquals(AMOUNT, debts.getFirst().getAmountOfDebt(), DebtConstants.EPSILON_OF_DEBT_SUMMARY,
            "When testing split with amounts that overpay" +
                "the new debt should be positive (friend overpaid by 50)");
    }

    private List<Debt> debts;
    private static final double AMOUNT = 50.00;
    private static final double AMOUNT_OVERPAYMENT = -30.00;
    private static final double AMOUNT_OVERPAYMENT_SPLIT = 80.00;
    private static final String REASON = "reason for payment";

}
