from gen import genData
from utils import *
from colorama import Fore, Back, Style, init
from chk import Parser
from tqdm import tqdm
import os
import sys

init(autoreset=True)

def test(jars, input):
    print(Fore.BLUE + "Input:")
    print(input)
    print()
    
    print(Fore.BLUE + "Input Without Sapce:")
    print(remove_space(input))
    print()
    
    try:
        sympy_res = sympy_result(input)
    except:
        with open('dirty_data.txt', 'a') as f:
            f.write(input + '\n\n')
        print(Back.RED + "Dirty Data! sympy_result() Failed!")
        return True
    print(Fore.BLUE + "Sympy Result:")
    print(sympy_res)
    print()
    
    for jar in jars:
        print(Fore.BLUE + jar + " Running...")
        result = run(jar, input)
        print(Fore.BLUE + jar + " Output:")
        if len(result) > 3000:
            print(result[:3000] + '...')
        else:
            print(result)
        print()
        
        try:
            Parser(result).check()
            print(Fore.GREEN + jar + " Correct Output Format!")
        except AssertionError as e:
            print(Back.RED + jar + " Wrong Output Format!")
            with open('wrong_data.txt', 'a', encoding='utf-8') as f:
                f.write(jar + "  Wrong Output Format!" + '\n' + input + '\n\n')
            return False
    
        try:
            if not equal(result, sympy_res):
                print(Back.RED + jar + " Wrong Answer!")
                with open('wrong_data.txt', 'a', encoding='utf-8') as f:
                    f.write(jar + "  Wrong Answer!" + '\n' + input + '\n\n')
                return False
            else:
                print(Fore.GREEN + jar + " Correct Answer!")
        except:
            with open('dirty_data.txt', 'a') as f:
                f.write(input + '\n\n')
            print(Back.RED + "Dirty Data! equal() Failed!")
            return True
    
    return True

def random_test(jars):
    print(Back.MAGENTA + '----------Random Test----------')
    print()
    casenum = 100
    for i in range(casenum):
        print(Back.CYAN + f'----------Test {i + 1}----------')
        data = genData()
        if not test(jars, data):
            return False
        print(Fore.GREEN + "Correct!")
        
    print(Back.MAGENTA + '=====================')
    print(Back.GREEN + f'All {casenum} random cases are correct!')
    return True

def value_test(jars):
    print(Back.MAGENTA + '----------Value Test----------')
    inputs = []
    with open('value_data.txt', 'r') as f:
        inputs = f.read().split('\n')
    input = ''
    cas = 0
    for i in range(len(inputs)):
        line = inputs[i]
        if line == '':
            if input == '':
                continue
            cas += 1
            print(Back.CYAN + f'----------Test {cas}----------')
            if not test(jars, input[:-1]):
                return
            print(Fore.GREEN + "Correct!")
            input = ''
        else:
            input += line + '\n'
    print(Back.MAGENTA + '=====================')
    print(Back.GREEN + f'All {cas} value cases are correct!')

def judge(jars):
    # value_test(jars)
    random_test(jars)

def calc(x):
    if x > 1.5:
        return 0
    return -31.8239 * x**4 + 155.9038 * x**3 - 279.2180 * x**2 + 214.0743 * x - 57.9370

if __name__ == '__main__':
    sys.setrecursionlimit(1000000)
    my_jar = '../project1/out/artifacts/project1_jar/project1.jar'
    # value_test([my_jar])``
    jars = []
    for file in os.listdir('./jars'):
        if file in []:
            continue
        jar_name = f'./jars/{file}'
        jars.append(jar_name)
    judge(jars)
    # print(calc(80 / 64))


# exp(4)*exp((-16*x^4-160*x^7-600*x^10-625*x^16-1000*x^13))^644204