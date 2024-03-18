import random
from utils import run, trim
from colorama import Fore, Back, Style, init
from chk import Parser

init(autoreset=True)

def handle_dirty_data(input, jar1, jar2):
    print('Input: ')
    print(trim(input))
    
    res1 = run(jar1, input)
    print(Fore.BLUE + jar1 + " result:")
    print(res1)
    try:
        Parser(res1).parseExpr()
    except AssertionError:
        print(Fore.RED + "Wrong Output Format: " + jar1)
        print()
        return False
    res2 = run(jar2, input + f' - ({res1})')
    if res2 != '0':
        print(Back.RED + "Wrong Answer!")
        print()
        return False
    
    print(Fore.GREEN + "Correct!")
    return True

def handle_txt(jar1, jar2):
    inputs = []
    with open('dirty_data.txt', 'r') as f:
        inputs = f.read().split('\n')
    input = ''
    # print(inputs)
    cas = 0
    lst = 0
    for i in range(len(inputs)):
        line = inputs[i]
        if line == '':
            if input == '':
                continue
            cas += 1
            print(Back.MAGENTA + f'----------Test {cas}----------')
            if not handle_dirty_data(input[:-1], jar1, jar2):
                with open('dirty_data.txt', 'w') as f:
                    f.write('\n'.join(inputs[lst:]) + '\n')
                return
            input = ''
            lst = i
        else:
            input += line + '\n'
    with open('dirty_data.txt', 'w') as f:
        f.write('')
        
if __name__ == '__main__':
    jar1 = 'project1.jar'
    jar2 = 'zyt.jar'
    handle_txt(jar1, jar1)
    