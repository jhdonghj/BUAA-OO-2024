import sympy
import re
import subprocess
import random
from math import *
from func_timeout import func_set_timeout

def remove_leading_zeros(s):
    return re.sub(r'\b([+-]?)(0+)(\d+)', lambda m: m.group(1) + m.group(3), s)

def remove_space(s):
    return s.replace(' ', '').replace('\t', '')

def trim(s):
    return remove_space(remove_leading_zeros(s))

@func_set_timeout(60)
def sympy_result(input):
    input = trim(input).replace('^', '**')
    fc = input.split('\n')
    
    x = sympy.Symbol('x')
    y = sympy.Symbol('y')
    z = sympy.Symbol('z')
    exp = sympy.exp
    f, g, h = None, None, None
    dx = lambda f: sympy.diff(f, x)
    eval_set = {
        'x': x, 'y': y, 'z': z,
        'exp': exp, 'E': exp(1),
        'f': f, 'g': g, 'h': h,
        'dx': dx
    }
    for k in fc[1:-1]:
        matcher = re.match(r'^(?P<name>[fgh])\((?P<param>.*?)\)=(?P<expr>.*)$', k)
        print(matcher.groupdict())
        expr_of_func = eval(matcher.groupdict()['expr'], eval_set)
        print('lambda ' + matcher.groupdict()['param'] + ': ' + str(expr_of_func))
        temp = eval('lambda ' + matcher.groupdict()['param'] + ': ' + str(expr_of_func), eval_set)
        if matcher.groupdict()['name'] == 'f':
            f = temp
            eval_set['f'] = f
        elif matcher.groupdict()['name'] == 'g':
            g = temp
            eval_set['g'] = g
        elif matcher.groupdict()['name'] == 'h':
            h = temp
            eval_set['h'] = h
        else:
            print('Error: Invalid function name')
    print(fc)
    print()
    return eval(fc[-1], eval_set)

def numeral_equal(result1, result2):
    for i in range(100):
        x = random.uniform(-100, 100)
        # exec(f'f = lambda x: x**2 + 1')
        res = eval(result1 + ' - (' + result2 + ')')
        if abs(res) > 1e-10:
            return False
    return True

@func_set_timeout(60)
def equal(result1, result2):
    print('Checking ...')
    comp_result = sympy.expand(result1).equals(result2)
    if comp_result == True:
        return True
    elif comp_result == None:
        print('Numerical comparison ...')
        return numeral_equal(result1, result2)
    else:
        return False

def run(jar_name, input):
    java_cmd = 'java -jar ' + jar_name
    process = subprocess.Popen(java_cmd,
                               stdin=subprocess.PIPE, 
                               stdout=subprocess.PIPE, 
                               stderr=subprocess.PIPE)
    stdout, stderr = process.communicate(input.encode('utf-8'))
    return stdout.decode('utf-8').strip()

