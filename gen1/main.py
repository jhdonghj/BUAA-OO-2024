from xeger import Xeger
import random
import subprocess

def data():
    n = random.randint(1, 10)
    var, val = [], []
    for i in range(n):
        v = Xeger(10).xeger(r'[a-zA-Z]+')
        while v in var:
            v = Xeger(10).xeger(r'[a-zA-Z]+')
        var.append(v)
        val.append(random.randint(-10, 10))
    print(var)
    output = ''
    output += str(n) + '\n'
    for i in range(n):
        output += var[i] + ' ' + str(val[i]) + '\n'
    len = 15
    def gen():
        if random.randint(0, 1):
            return random.choice(var)
        else:
            return str(random.randint(0, 10))
    expr = gen()
    for i in range(len):
        expr += ' ' + random.choice("+-*") + ' ' + gen()
    output += expr
    print(output)
    expr = ' ' + expr + ' '
    for i in range(n):
        expr = expr.replace(' ' + var[i] + ' ', ' ' + str(val[i]) + ' ')
    print(eval(expr))
    return output, eval(expr)

for i in range(100):
    jar_name = 'run.jar'
    java_cmd = ['java', '-jar', jar_name]

    p = subprocess.Popen(java_cmd, stdin=subprocess.PIPE, stdout=subprocess.PIPE, stderr=subprocess.PIPE)
    output, result = data()
    stdout, stderr = p.communicate(input=output.encode())
    stdout = stdout.decode().strip()

    print(stdout)
    if int(stdout) == result:
        print("OK")
    else:
        print("WA")
        break
