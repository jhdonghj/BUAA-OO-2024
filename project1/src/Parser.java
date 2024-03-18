import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;

public class Parser {
    private Lexer lexer;
    private HashMap<String, Funct> functs;

    public Parser() {
        functs = new HashMap<>();
    }

    public void setLexer(Lexer lexer) {
        this.lexer = lexer;
    }

    public Expr parseExpr() {
        Expr expr = new Expr();
        long flag = 1;
        if (lexer.peekMatch("[+-]")) {
            if (lexer.peekMatch("-")) {
                flag *= -1;
            }
            lexer.next();
        }
        expr._addExpr(parseTerm(flag));
        while (lexer.peekMatch("[+-]")) {
            flag = 1;
            if (lexer.peekMatch("-")) {
                flag *= -1;
            }
            lexer.next();
            expr._addExpr(parseTerm(flag));
        }
        return expr;
    }

    public Expr parseTerm(long flagPar) {
        Expr expr = Expr.One();
        long flag = flagPar;
        if (lexer.peekMatch("[+-]")) {
            if (lexer.peekMatch("-")) {
                flag *= -1;
            }
            lexer.next();
        }
        expr = expr.mulExpr(parseFactor());
        while (lexer.peekMatch("\\*")) {
            lexer.next();
            expr = expr.mulExpr(parseFactor());
        }
        if (flag == -1) {
            expr._neg();
        }
        return expr;
    }

    public Expr parseFactor() {
        if (lexer.peekMatch("\\(")) { // 表达式
            return parseExprFactor();
        } else if (lexer.peekMatch("[xyz]")) { // 幂函数
            return parsePowFactor();
        } else if (lexer.peekMatch("exp")) { // 指数函数
            return parseExpFactor();
        } else if (lexer.peekMatch("[fgh]")) { // 自定义函数
            return parseFunctFactor();
        } else if (lexer.peekMatch("dx")) { // 求导
            return parseDerivative();
        } else { // 常数
            return parseConst();
        }
    }

    public Expr parseDerivative() {
        lexer.next(); // "dx"
        lexer.next(); // "("
        Expr expr = parseExpr();
        lexer.next(); // ")"
        return expr.derivative();
    }

    public Expr parseExprFactor() {
        lexer.next();
        Expr expr = parseExpr();
        lexer.next();
        BigInteger exponent = parseExponent();
        return expr.exp(exponent);
    }

    public Expr parsePowFactor() {
        String variable = lexer.peek();
        lexer.next();
        BigInteger exponent = parseExponent();
        BasicTerm basicTerm = new BasicTerm();
        basicTerm._mulPow(variable, exponent);
        return new Expr(new Term(basicTerm, BigInteger.valueOf(1)));
    }

    public Expr parseExpFactor() {
        lexer.next(); // "exp"
        lexer.next(); // "("
        Expr factor = parseFactor(); // 因子
        lexer.next(); // ")"
        BigInteger exponent = parseExponent();
        factor = factor.mulTerm(new Term(new BasicTerm(), exponent));
        BasicTerm basicTerm = new BasicTerm();
        basicTerm._mulExp(factor);
        return new Expr(new Term(basicTerm, BigInteger.valueOf(1)));
    }

    public Expr parseFunctFactor() {
        final String name = lexer.peek();
        lexer.next(); // "fgh"
        ArrayList<Expr> args = new ArrayList<>();
        while (!lexer.peekMatch("\\)")) {
            lexer.next(); // "(" or ","
            args.add(parseFactor());
        }
        lexer.next(); // ")"
        return functs.get(name).apply(args);
    }

    public Expr parseConst() {
        int flag = 1;
        if (lexer.peekMatch("[+-]")) {
            if (lexer.peekMatch("-")) {
                flag *= -1;
            }
            lexer.next();
        }
        BigInteger value = new BigInteger(lexer.peek());
        lexer.next();
        return new Expr(new Term(new BasicTerm(), value.multiply(BigInteger.valueOf(flag))));
    }

    public BigInteger parseExponent() {
        if (!lexer.peekMatch("\\^")) {
            return BigInteger.valueOf(1);
        }
        lexer.next();
        if (lexer.peekMatch("\\+")) {
            lexer.next();
        }
        BigInteger exponent = new BigInteger(lexer.peek());
        lexer.next();
        return exponent;
    }

    public void addFunct(String name, Funct funct) {
        functs.put(name, funct);
    }

    public Funct parseFunct() {
        Funct funct = new Funct();
        lexer.next(); // "("
        while (!lexer.peekMatch("=")) {
            funct.addVar(lexer.peek());
            lexer.next(); // var
            lexer.next(); // "," or ")"
        }
        lexer.next(); // "="
        funct.setExpr(parseExpr());
        return funct;
    }
}
