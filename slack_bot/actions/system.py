import subprocess
from .settings import database_passwd 

def _exec(args):
    # helper function
    return subprocess.run(args, stdout=subprocess.PIPE).stdout.decode('utf-8')


def df():
    # disk free
    return _exec(["df", "-h"])


def free():
    # ram etc.
    return _exec(["free", "-h"])


def restart_spring_dev():
    # must be executed with gitlab-runner user
    return _exec(["sudo", "systemctl", "restart" "stuprojavaserver_dev"])


def restart_spring():
    return _exec(["sudo", "systemctl", "restart" "stuprojavaserver"])


def restart_flask():
    return _exec(["sudo", "systemctl", "restart" "stupropython_dev"])


def clear_database_dev():
    return _exec(["mysql", "-u", "root", "-h", "127.0.0.1", "-P", "3306", "--protocol", "tcp", f"-p'{database_passwd}'", "-e", "'drop database querimonia_dev; create database querimonia_dev'"])


def clear_database():
    return _exec(["mysql", "-u", "root", "-h", "127.0.0.1", "-P", "3306", "--protocol", "tcp", f"-p'{database_passwd}'", "-e", "'drop database querimonia_db; create database querimonia_db'"])