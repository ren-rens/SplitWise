package bg.sofia.uni.fmi.mjt.splitwise.server.model.payments.debt;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

public class Debt implements Serializable {

    @Serial
    private static final long serialVersionUID = 5L;

    public Debt(double amountOfDept, String reasonForPayment) {
        this.amountOfDebt = amountOfDept;
        this.reasonForPayment = reasonForPayment;
    }

    public double getAmountOfDebt() {
        return this.amountOfDebt;
    }

    public String getReasonForPayment() {
        return this.reasonForPayment;
    }

    public void updateDepth(double repayment) {
        this.amountOfDebt += repayment;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Debt debt = (Debt) o;
        return Double.compare(amountOfDebt, debt.amountOfDebt) == 0 &&
            Objects.equals(reasonForPayment, debt.reasonForPayment);
    }

    @Override
    public int hashCode() {
        return Objects.hash(amountOfDebt, reasonForPayment);
    }

    private double amountOfDebt;
    private final String reasonForPayment;

}
