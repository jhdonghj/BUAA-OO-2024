import java.math.BigInteger;

public class Term {
    private BasicTerm variable;
    private BigInteger coef;
    private String str = null;

    public Term() {
        variable = new BasicTerm();
        coef = new BigInteger("1");
    }

    public Term(BasicTerm variable, BigInteger coef) {
        this.variable = new BasicTerm(variable);
        this.coef = coef;
    }

    public BasicTerm getVar() {
        return variable;
    }

    public BigInteger getCoef() {
        return coef;
    }

    public Term mulTerm(Term term) {
        return new Term(term.getVar().mulBT(this.variable),
                        term.getCoef().multiply(this.coef));
    }

    public boolean isExpr() {
        int cnt = 0;
        if (coef.compareTo(BigInteger.ONE) != 0) {
            cnt++;
        }
        cnt += variable.getPow().size();
        if (!variable.getExpVar().equals(Expr.Zero())) {
            cnt++;
        }
        return cnt > 1;
    }

    public String toString() {
        if (str != null) {
            return str;
        }
        StringBuilder sb = new StringBuilder();
        boolean coe = coef.abs().compareTo(BigInteger.ONE) != 0;
        boolean var = !variable.isOne();
        if (coe || !var) {
            sb.append(coef.abs());
        }
        if (coe && var) {
            sb.append("*");
        }
        if (var) {
            sb.append(variable);
        }
        if (Main.isEnd()) {
            str = sb.toString();
        }
        return sb.toString();
    }
}
