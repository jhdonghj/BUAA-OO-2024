from utils import trim

class lexer:
    def __init__(self, s : str) -> None:
        self.s = s
        self.pos = 0
        self.curToken = ''
        
    def next(self):
        if self.pos >= len(self.s):
            return None
        if str.isdigit(self.s[self.pos]):
            start = self.pos
            while self.pos < len(self.s) and str.isdigit(self.s[self.pos]):
                self.pos += 1
            self.curToken = self.s[start:self.pos]
        elif self.s[self.pos] == 'e':
            assert self.s[self.pos + 1] == 'x' and self.s[self.pos + 2] == 'p'
            self.pos += 3
            self.curToken = 'exp'
        else:
            self.curToken = self.s[self.pos]
            self.pos += 1

    def peek(self):
        return self.curToken
    
class Parser:
    def __init__(self, s : str) -> None:
        s = trim(s)
        self.lex = lexer(s)
        self.lex.next()
        
    def check(self):
        self.parseExpr()
        assert self.lex.pos >= len(self.lex.s)
    
    def parseExpr(self):
        if self.lex.peek() == '+' or self.lex.peek() == '-':
            self.lex.next()
        self.parseTerm()
        while self.lex.peek() == '+' or self.lex.peek() == '-':
            self.lex.next()
            self.parseTerm()
    
    def parseTerm(self):
        if self.lex.peek() == '+' or self.lex.peek() == '-':
            self.lex.next()
        self.parseFactor()
        while self.lex.peek() == '*':
            self.lex.next()
            self.parseFactor()
    
    def parseFactor(self):
        if self.lex.peek() == 'x':
            self.parsePow()
        elif self.lex.peek() == 'exp':
            self.parseExp()
        else:
            self.parseConst()
    
    def parsePow(self):
        self.lex.next()
        if self.lex.peek() == '^':
            self.lex.next()
            self.parseConst()
    
    def parseExp(self):
        self.lex.next() # exp
        self.lex.next() # (
        if self.lex.peek() == '(': # exp((expr))
            self.lex.next() # (
            self.parseExpr() # expr
            assert self.lex.peek() == ')'
            self.lex.next() # )
        elif self.lex.peek() == 'x':
            self.parsePow()
        elif self.lex.peek() == 'exp':
            self.parseExp()
        else:
            self.parseConst()
        
        assert self.lex.peek() == ')'
        self.lex.next()
        
        if self.lex.peek() == '^':
            self.lex.next()
            if self.lex.peek() == '+':
                self.lex.next()
            assert self.lex.peek().isdigit()
            self.lex.next()
    
    def parseConst(self):
        if self.lex.peek() == '+' or self.lex.peek() == '-':
            self.lex.next()
        assert self.lex.peek().isdigit()
        self.lex.next()


if __name__ == "__main__":
    s = "exp(exp(x^64)^4)^5"
    p = Parser(s)
    p.parseExpr()
    print('done')