package Factor;

public class Exp implements Factor {
    private Factor factor;

    public Exp(Factor factor) {
        this.factor = factor;
    }

    @Override
    public String toString() {
        return "exp(" + factor.toString() + ")";
    }

    @Override
    public Factor derive() {
        Term term = new Term();
        /* TODO 3 */
        term.addFactor(factor.derive());
        term.addFactor(this.clone());
        return term;
    }

    @Override
    public Factor clone() {
        return new Exp(factor.clone());
    }
}
