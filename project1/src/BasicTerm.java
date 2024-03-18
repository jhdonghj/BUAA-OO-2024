import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;

public class BasicTerm {
    private HashMap<String, BigInteger> pow;
    private Expr expVar;
    private String str = null;

    public BasicTerm() {
        pow = new HashMap<>();
        expVar = Expr.Zero();
    }

    public BasicTerm(BasicTerm bt) {
        pow = new HashMap<>(bt.getPow());
        expVar = new Expr(bt.getExpVar());
    }

    public HashMap<String, BigInteger> getPow() {
        return pow;
    }

    public Expr getExpVar() {
        return expVar;
    }

    /**
     * @return true if this is 1
     */
    public boolean isOne() {
        return pow.isEmpty() && expVar.equals(Expr.Zero());
    }

    public void _mulPow(String variable, BigInteger exponent) {
        if (!pow.containsKey(variable)) {
            pow.put(variable, BigInteger.valueOf(0));
        }
        pow.put(variable, pow.get(variable).add(exponent));
        if (pow.get(variable).equals(BigInteger.valueOf(0))) {
            pow.remove(variable);
        }
    }

    public void _mulExp(Expr factor) {
        expVar = expVar.addExpr(factor);
    }

    public BasicTerm mulBT(BasicTerm bt) {
        BasicTerm result = new BasicTerm(this);
        bt.getPow().forEach((k, v) -> result._mulPow(k, v));
        result._mulExp(bt.getExpVar());
        return result;
    }

    public Expr substitute(HashMap<String, Expr> map) {
        Expr expr = Expr.One();
        pow.forEach((k, v) -> {
            expr._mulExpr(map.get(k).exp(v));
        });
        BasicTerm basicTerm = new BasicTerm();
        basicTerm._mulExp(expVar.substitute(map));
        expr._mulTerm(new Term(basicTerm, BigInteger.valueOf(1)));
        return expr;
    }

    public Expr derivative() {
        Expr expr = Expr.Zero();
        if (!pow.isEmpty()) {
            assert pow.size() == 1;
            String variable = pow.keySet().iterator().next();
            assert variable.equals("x");
            BigInteger exponent = pow.get(variable);
            BasicTerm bt = new BasicTerm();
            bt._mulExp(expVar);
            bt._mulPow(variable, exponent.subtract(BigInteger.valueOf(1)));
            expr._addTerm(new Term(bt, exponent));
        }
        if (!expVar.equals(Expr.Zero())) {
            Expr temp = expVar.derivative();
            temp._mulTerm(new Term(new BasicTerm(this), BigInteger.valueOf(1)));
            expr._addExpr(temp);
        }
        return expr;
    }

    public String origin() {
        StringBuilder sb = new StringBuilder();
        sb.append("exp(");
        if (expVar.isExpr()) {
            sb.append("(");
            sb.append(expVar);
            sb.append(")");
        } else {
            sb.append(expVar);
        }
        sb.append(")");
        return sb.toString();
    }

    public String gcd() {
        BigInteger gcd = expVar.coefGcd();
        expVar._divInt(gcd);
        StringBuilder sb = new StringBuilder();
        sb.append("exp(");
        if (expVar.isExpr()) {
            sb.append("(");
            sb.append(expVar);
            sb.append(")");
        } else {
            sb.append(expVar);
        }
        sb.append(")^");
        sb.append(gcd);
        expVar._mulInt(gcd);
        return sb.toString();
    }

    public String pivot() {
        ArrayList<String> strs = new ArrayList<>();
        ArrayList<BigInteger> nums = new ArrayList<>();
        expVar.getTerms().forEach((k, v) -> {
            strs.add(k.toString());
            nums.add(v);
        });
        HashSet<BigInteger> ints = new HashSet<>();
        nums.forEach((v) -> ints.add(v.abs()));
        if (ints.size() > 10) {
            return null;
        }
        String res = "$";
        for (BigInteger pivot : ints) {
            if (pivot.compareTo(BigInteger.ZERO) < 0) {
                continue;
            }
            // (t-0.3)*pivot ~ (t+0.5)*pivot
            final BigInteger lim = pivot.divide(BigInteger.valueOf(3));
            ArrayList<String> str1 = new ArrayList<>();
            ArrayList<String> str2 = new ArrayList<>();
            ArrayList<BigInteger> num1 = new ArrayList<>();
            ArrayList<BigInteger> num2 = new ArrayList<>();
            for (int i = 0; i < strs.size(); i++) {
                BigInteger now = nums.get(i);
                BigInteger t = now.add(lim).divide(pivot);
                BigInteger rem = now.subtract(t.multiply(pivot));
                if (rem.compareTo(BigInteger.ZERO) != 0) {
                    str1.add(strs.get(i));
                    num1.add(rem);
                }
                if (t.compareTo(BigInteger.ZERO) != 0) {
                    str2.add(strs.get(i));
                    num2.add(t);
                }
            }
            StringBuilder sb = new StringBuilder();
            if (!str1.isEmpty()) {
                sb.append(getExpr(str1, num1));
                if (!str2.isEmpty()) {
                    sb.append("*");
                }
            }
            if (!str2.isEmpty()) {
                sb.append(getExpr(str2, num2));
                sb.append("^");
                sb.append(pivot);
            }
            res = min(res, sb.toString());
        }
        return res;
    }

    public String min(String a, String b) {
        if (a.equals("$") || a.length() > b.length()) {
            return b;
        } else {
            return a;
        }
    }

    public boolean chkExpr(ArrayList<String> strs, ArrayList<BigInteger> nums) {
        if (strs.size() > 1) {
            return true;
        }
        int cnt = 0;
        if (nums.get(0).compareTo(BigInteger.ONE) != 0) {
            cnt++;
        }
        if (strs.get(0).contains("x")) {
            cnt++;
        }
        if (strs.get(0).contains("exp")) {
            cnt++;
        }
        return cnt > 1;
    }

    public String getExpr(ArrayList<String> str, ArrayList<BigInteger> num) {
        StringBuilder sb = new StringBuilder();
        boolean expr = chkExpr(str, num);
        sb.append("exp(");
        if (expr) {
            sb.append('(');
        }
        int pos = -1;
        for (int i = 0; i < str.size(); i++) {
            if (num.get(i).compareTo(BigInteger.ZERO) > 0) {
                pos = i;
                break;
            }
        }
        if (pos != -1 && pos != 0) {
            String tmp = str.get(0);
            str.set(0, str.get(pos));
            str.set(pos, tmp);
            BigInteger tmp2 = num.get(0);
            num.set(0, num.get(pos));
            num.set(pos, tmp2);
        }
        for (int i = 0; i < str.size(); i++) {
            if (num.get(i).compareTo(BigInteger.ZERO) < 0) {
                sb.append('-');
            } else if (i != 0) {
                sb.append('+');
            }
            boolean coe = num.get(i).abs().compareTo(BigInteger.ONE) != 0;
            boolean var = !str.get(i).isEmpty();
            if (coe || !var) {
                sb.append(num.get(i).abs());
            }
            if (coe && var) {
                sb.append('*');
            }
            if (var) {
                sb.append(str.get(i));
            }
        }
        if (expr) {
            sb.append(')');
        }
        sb.append(')');
        return sb.toString();
    }

    public String sipmExp() {
        String origin = origin();
        String gcd = gcd();
        String pivot = pivot();
        // String pivot = null;
        if (pivot == null) {
            if (origin.length() <= gcd.length()) {
                return origin;
            } else {
                return gcd;
            }
        } else {
            if (origin.length() <= gcd.length() && origin.length() <= pivot.length()) {
                return origin;
            } else if (gcd.length() <= origin.length() && gcd.length() <= pivot.length()) {
                return gcd;
            } else {
                return pivot;
            }
        }
    }

    public String toString() {
        if (str != null) {
            return str;
        }
        StringBuilder sb = new StringBuilder();
        ArrayList<String> vars = new ArrayList<>(pow.keySet());
        ArrayList<BigInteger> exps = new ArrayList<>(pow.values());
        for (int i = 0; i < vars.size(); i++) {
            if (i != 0) {
                sb.append("*");
            }
            sb.append(vars.get(i));
            if (exps.get(i).compareTo(BigInteger.ONE) > 0) {
                sb.append("^");
                sb.append(exps.get(i));
            }
        }
        if (!expVar.equals(Expr.Zero())) {
            if (!pow.isEmpty()) {
                sb.append("*");
            }
            sb.append(sipmExp());
        }
        if (Main.isEnd()) {
            str = sb.toString();
        }
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        BasicTerm that = (BasicTerm) o;
        return this.pow.equals(that.getPow()) && this.expVar.equals(that.getExpVar());
    }

    @Override
    public int hashCode() {
        return Objects.hash(pow, expVar);
    }
}
