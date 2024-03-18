import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    private static boolean isEnd = false;

    public static boolean isEnd() {
        return isEnd;
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int n;
        n = scanner.nextInt();
        scanner.nextLine();
        ArrayList<String> functInputs = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            functInputs.add(scanner.nextLine());
        }
        String input = scanner.nextLine();

        final long start = System.currentTimeMillis();

        Parser parser = new Parser();
        for (int i = 0; i < n; i++) {
            Lexer lexerFunct = new Lexer(functInputs.get(i));
            String functName = lexerFunct.peek();
            lexerFunct.next();
            parser.setLexer(lexerFunct);
            parser.addFunct(functName, parser.parseFunct());
        }

        Lexer lexer = new Lexer(input);
        parser.setLexer(lexer);
        Expr expr = parser.parseExpr();
        isEnd = true;
        System.out.println(expr);
        System.err.println(System.currentTimeMillis() - start + "ms");
    }
}