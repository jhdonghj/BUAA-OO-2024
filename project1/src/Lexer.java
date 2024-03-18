public class Lexer {
    private final String input;
    private int pos = 0;
    private String curToken;

    public Lexer(String input) {
        this.input = input.replaceAll("[ \t]", "");
        this.next();
    }

    private String getNumber() {
        StringBuilder sb = new StringBuilder();
        while (pos < input.length() && Character.isDigit(input.charAt(pos))) {
            sb.append(input.charAt(pos));
            ++pos;
        }

        return sb.toString();
    }

    public void next() {
        if (pos == input.length()) {
            return;
        }

        char c = input.charAt(pos);
        if (Character.isDigit(c)) {
            curToken = getNumber();
        } else if (c == 'e') {
            assert input.charAt(pos + 1) == 'x' && input.charAt(pos + 2) == 'p';
            pos += 3;
            curToken = "exp";
        } else if (c == 'd') {
            assert input.charAt(pos + 1) == 'x';
            pos += 2;
            curToken = "dx";
        } else {
            pos++;
            curToken = String.valueOf(c);
        }
    }

    public String peek() {
        return curToken;
    }

    public boolean peekMatch(String regex) {
        return curToken.matches(regex);
    }
}
