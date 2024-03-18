import java.util.ArrayList;
import java.util.HashMap;

public class Funct {
    private ArrayList<String> vars;
    private Expr expr;

    public Funct() {
        vars = new ArrayList<>();
        expr = new Expr();
    }

    public void addVar(String var) {
        vars.add(var);
    }

    public void setExpr(Expr expr) {
        this.expr = expr;
    }

    public Expr apply(ArrayList<Expr> args) {
        HashMap<String, Expr> map = new HashMap<>();
        for (int i = 0; i < vars.size(); i++) {
            map.put(vars.get(i), args.get(i));
        }
        return expr.substitute(map);
    }
}
