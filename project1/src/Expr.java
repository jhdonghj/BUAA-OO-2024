import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;

public class Expr {
    private HashMap<BasicTerm, BigInteger> terms;
    private String str = null;

    public Expr() {
        terms = new HashMap<>();
    }

    public Expr(Term term) {
        terms = new HashMap<>();
        _addTerm(term);
    }

    public Expr(Expr expr) {
        terms = new HashMap<>(expr.getTerms());
    }

    public static Expr Zero() {
        return new Expr();
    }

    public static Expr One() {
        Expr expr = new Expr();
        expr._addTerm(new Term(new BasicTerm(), BigInteger.valueOf(1)));
        return expr;
    }

    public HashMap<BasicTerm, BigInteger> getTerms() {
        return terms;
    }

    public void _neg() {
        terms.forEach((k, v) -> terms.put(k, v.negate()));
    }

    public void _addTerm(Term term) {
        if (!terms.containsKey(term.getVar())) {
            terms.put(term.getVar(), BigInteger.valueOf(0));
        }
        terms.put(term.getVar(), terms.get(term.getVar()).add(term.getCoef()));
        if (terms.get(term.getVar()).equals(BigInteger.valueOf(0))) {
            terms.remove(term.getVar());
        }
    }

    public void _addExpr(Expr expr) {
        expr.getTerms().forEach((k, v) -> _addTerm(new Term(k, v)));
    }

    public Expr addExpr(Expr expr) {
        Expr result = new Expr(this);
        result._addExpr(expr);
        return result;
    }

    public void _mulInt(BigInteger val) {
        terms.forEach((k, v) -> terms.put(k, v.multiply(val)));
    }

    public Expr mulInt(BigInteger val) {
        Expr result = new Expr(this);
        result._mulInt(val);
        return result;
    }

    public void _divInt(BigInteger val) {
        terms.forEach((k, v) -> terms.put(k, v.divide(val)));
    }

    public Expr divInt(BigInteger val) {
        Expr result = new Expr(this);
        result._divInt(val);
        return result;
    }

    public Expr mulTerm(Term term) {
        Expr result = Expr.Zero();
        this.terms.forEach((k, v) -> result._addTerm(term.mulTerm(new Term(k, v))));
        return result;
    }

    public void _mulTerm(Term term) {
        terms = mulTerm(term).getTerms();
    }

    public Expr mulExpr(Expr expr) {
        Expr result = Expr.Zero();
        ArrayList<Term> ts1 = new ArrayList<>();
        ArrayList<Term> ts2 = new ArrayList<>();
        this.terms.forEach((k, v) -> ts1.add(new Term(k, v)));
        expr.getTerms().forEach((k, v) -> ts2.add(new Term(k, v)));
        for (Term t1 : ts1) {
            for (Term t2 : ts2) {
                result._addTerm(t1.mulTerm(t2));
            }
        }
        return result;
    }

    public void _mulExpr(Expr expr) {
        terms = mulExpr(expr).getTerms();
    }

    public Expr exp(BigInteger exponent) {
        Expr result = Expr.One();
        Expr a = new Expr(this);
        BigInteger b = exponent;
        while (b.compareTo(BigInteger.ZERO) > 0) {
            if (b.testBit(0)) {
                result = result.mulExpr(a);
            }
            b = b.shiftRight(1);
            if (b.compareTo(BigInteger.ZERO) == 0) {
                break;
            }
            a = a.mulExpr(a);
        }
        return result;
    }

    public Expr substitute(HashMap<String, Expr> map) {
        Expr expr = new Expr();
        terms.forEach((k, v) -> {
            k.substitute(map).getTerms().forEach((kk, vv) -> {
                expr._addTerm(new Term(kk, vv.multiply(v)));
            });
        });
        return expr;
    }

    public Expr derivative() {
        Expr result = Expr.Zero();
        terms.forEach((k, v) -> {
            result._addExpr(k.derivative().mulInt(v));
        });
        return result;
    }

    /**
     * @return true if this must be recognized as an expr
     */
    public boolean isExpr() {
        if (terms.size() >= 2) {
            return true;
        } else if (terms.isEmpty()) {
            return false;
        } else {
            ArrayList<BasicTerm> bts = new ArrayList<>(terms.keySet());
            ArrayList<BigInteger> coefs = new ArrayList<>(terms.values());
            Term term = new Term(bts.get(0), coefs.get(0));
            return term.isExpr();
        }
    }

    public int length() {
        int len = 0;
        int flag = 0;
        for (BasicTerm bt : terms.keySet()) {
            BigInteger coef = terms.get(bt);
            if (bt.isOne()) {
                len += coef.toString().length();
            } else if (coef.abs().compareTo(BigInteger.ONE) != 0) {
                len += coef.toString().length() + 1;
            }
            if (coef.compareTo(BigInteger.ZERO) < 0) {
                len--;
            } else {
                flag = 1;
            }
        }
        if (flag == 0) {
            len++;
        }
        if (isExpr()) {
            len += 2;
        }
        return len;
    }

    public BigInteger coefGcd() {
        BigInteger gcd = BigInteger.ZERO;
        for (BigInteger coef : terms.values()) {
            gcd = gcd.gcd(coef.abs());
        }
        return gcd;
    }

    public boolean equals(Expr expr) {
        return terms.equals(expr.getTerms());
    }

    public int hashCode() {
        return terms.hashCode();
    }

    public String toString() {
        if (terms.isEmpty()) {
            return "0";
        }
        if (str != null) {
            return str;
        }
        StringBuilder sb = new StringBuilder();
        ArrayList<BasicTerm> bts = new ArrayList<>(terms.keySet());
        ArrayList<BigInteger> coefs = new ArrayList<>(terms.values());
        int fir = 0;
        for (int i = 0; i < bts.size(); i++) {
            if (coefs.get(i).compareTo(BigInteger.ZERO) >= 0) {
                fir = i;
                break;
            }
        }
        if (coefs.get(fir).compareTo(BigInteger.ZERO) < 0) {
            sb.append("-");
        }
        sb.append(new Term(bts.get(fir), coefs.get(fir)));
        for (int i = 0; i < bts.size(); i++) {
            if (i != fir) {
                if (coefs.get(i).compareTo(BigInteger.ZERO) >= 0) {
                    sb.append("+");
                } else {
                    sb.append("-");
                }
                sb.append(new Term(bts.get(i), coefs.get(i)));
            }
        }
        if (Main.isEnd()) {
            str = sb.toString();
        }
        return sb.toString();
    }
}
