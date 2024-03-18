from subprocess import Popen, PIPE

if __name__ == '__main__':
    while True:
        p = Popen('python main.py', shell=True)
        p.wait()
        print('Restarting ...')
