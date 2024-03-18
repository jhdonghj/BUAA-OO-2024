from random import *
from utils import trim

def expr(dep, depDist, functDist, vars, maxNum, maxExp, funGen):
    """根据参数生成表达式

    Args:
        dep (int): 当前深度
        depDist (list int): 不同深度下停止的概率分布
        functDist (dist {name: vars}): 可选函数
        vars (list str): 可选变量
        maxNum (int): 每一层最大项数
        maxExp (int): 最大指数
        funGen (bool): 是否正在生成自定义函数

    Returns:
        str: 生成的表达式
    """
    res = blank()
    if randint(0, 1):
        res += addsub() + blank()
    termNum = randint(1, maxNum)
    res += term(dep, depDist, functDist, vars, termNum, maxExp, funGen) + blank()
    for i in range(termNum - 1):
        res += addsub() + blank() + term(dep, depDist, functDist, vars, maxNum, maxExp, funGen) + blank()
    return res

def term(dep, depDist, functDist, vars, maxNum, maxExp, funGen):
    res = ''
    if randint(0, 1):
        res += addsub() + blank()
    factorNum = randint(1, maxNum)
    res += factor(dep, depDist, functDist, vars, maxNum, maxExp, funGen)
    for i in range(factorNum - 1):
        res += blank() + '*' + blank() + factor(dep, depDist, functDist, vars, maxNum, maxExp, funGen)
    return res

# 常数因子 | 表达式因子 | 幂函数 | 指数函数 | 自定义函数调用 | 求导因子
def factor(dep, depDist, functDist, vars, maxNum, maxExp, funGen):
    if uniform(0, 1) <= depDist[dep]:
        fl = randint(0, 1)
        if fl == 0:
            return signed_integer(0, 10**6)
        elif fl == 1:
            return pow(vars, funGen)
    ls = [0, 1]
    if not funGen:
        ls.append(2)
    if len(functDist) > 0:
        ls.append(3)
    fl = choice(ls)
    if fl == 0:
        return exprFactor(dep + 1, depDist, functDist, vars, maxNum, maxExp, funGen)
    elif fl == 1:
        return exp(dep + 1, depDist, functDist, vars, maxNum, maxExp, funGen)
    elif fl == 2:
        return derivative(dep + 1, depDist, functDist, vars, maxNum, maxExp, funGen)
    else:
        return functCall(dep + 1, depDist, functDist, vars, maxNum, maxExp, funGen)

def derivative(dep, depDist, functDist, vars, maxNum, maxExp, funGen):
    return 'dx' + blank() + '(' + blank() + expr(dep, depDist, functDist, vars, maxNum, maxExp, funGen) \
                + blank() + ')'

def exprFactor(dep, depDist, functDist, vars, maxNum, maxExp, funGen):
    return '(' + blank() + expr(dep, depDist, functDist, vars, maxNum, maxExp, funGen) \
                + blank() + ')' + (exponent(maxExp) if randint(0, 1) else '')

def exp(dep, depDist, functDist, vars, maxNum, maxExp, funGen):
    return 'exp' + blank() + '(' + blank() + factor(dep, depDist, functDist, vars, maxNum, maxExp, funGen) \
                + blank() + ')' + (exponent(maxExp) if randint(0, 1) else '')

def exponent(maxExp):
    return '^' + blank() + ('+' if randint(0, 1) else '' ) + integer(0, maxExp)

def integer(l, r):
    return "0" * randint(0, 3) + str(randint(l, r))

def signed_integer(l, r):
    return choice(['+', '-', '']) + integer(l, r)

def blank():
    res = ''
    for i in range(randint(0, 3)):
        res += choice([' ', '\t'])
    return res

def addsub():
    return choice(['+', '-'])

def pow(vars, funGen):
    return choice(vars) + (exponent(4 if funGen else 100) if randint(0, 1) else '')

def functCall(dep, depDist, functDist, vars, maxNum, maxExp, funGen):
    # print(functDist)
    functName, functVars = choice(list(functDist.items()))
    res = functName + blank() + '('
    for i in range(len(functVars)):
        res += blank() + factor(dep, depDist, functDist, vars, maxNum, maxExp, funGen) + blank()
        res += ',' if i != len(functVars) - 1 else ')'
    return res
    
def functDef(funName, vars, depDist, maxNum, maxExp, functDist):
    res = funName + blank() + '('
    for var in vars:
        res += blank() + var + blank()
        res += ',' if var != vars[-1] else ')'
    res += blank() + '=' + blank() + expr(0, depDist, functDist, vars, maxNum, maxExp, 1)
    return res

def genData():
    temp = [
        "2",
        "h(y,x)=(---000294594)^00",
        "g(y)=-exp(y^0002)",
        "-g((-+0570028*x*+914242--x^+089*x-x*x^+0076*x)^+0005)*g(x)-x^040++g(x^006)*exp(x^0073)*exp(g(+000241268))"
    ]
    # return '\n'.join(temp)
    res = []
    n = randint(0, 3)
    # print('Number of functions:', n)
    res.append(str(n))
    funNames = sample(['f', 'g', 'h'], k=n)
    functDist = {}
    for funName in funNames:
        vars = sample(['x', 'y', 'z'], k=randint(1, 3))
        res.append(functDef(funName, vars, [0.3, 1], 1, 3, functDist))
        functDist[funName] = vars
    res.append(expr(0, [0.1, 0.5, 1], functDist, ['x'], 3, 5, 0))
    return '\n'.join(res)

if __name__ == '__main__':
    print(trim(genData()))

