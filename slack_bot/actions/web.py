import requests
from . import settings


def http_status_code() -> str:
    status_python      = ""
    status_java_dev    = ""
    status_java_master = ""
    status_kikuko      = ""
    try:
        status_python      = requests.post("https://querimonia.iao.fraunhofer.de/python/sentiment_analyse", auth=settings.querimonia_cred, timeout=5).status_code
        status_java_dev    = requests.get("https://querimonia.iao.fraunhofer.de/dev/api/complaints?page=0", auth=settings.querimonia_cred, timeout=5).status_code
        status_java_master = requests.get("https://querimonia.iao.fraunhofer.de/api/complaints?page=0", auth=settings.querimonia_cred, timeout=5).status_code
        status_kikuko = requests.post("https://kikuko.iao.fraunhofer.de/apitext",  auth=settings.kikuko_cred, timeout=5)
    except requests.exceptions.ReadTimeout as e:
        status_kikuko = e
    return "\nSystemstatus: \n"+ "- Java dev:      " + str(status_java_dev) + "\n" "- Java master: " + str(status_java_master) + "\n" "- Python:        " + str(status_python)+ "\n" "- Kikuko:        " + str(status_kikuko)


def reset_dev() -> str:
    return str(requests.post("https://querimonia.iao.fraunhofer.de/dev/api/reset", auth=settings.querimonia_cred, timeout=5).status_code)


def reset() -> str:
    return str(requests.post("https://querimonia.iao.fraunhofer.de/api/reset", auth=settings.querimonia_cred, timeout=5).status_code)