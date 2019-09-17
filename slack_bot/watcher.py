import requests
import logging
import slack
import os
import time
from datetime import datetime
from pytz import timezone
from actions import web

logging.basicConfig(format='%(asctime)s - %(name)s - %(levelname)s - %(message)s', level=logging.INFO)
logger = logging.getLogger(__name__)
client = slack.WebClient(token=os.environ['SLACK_API_TOKEN'])




# functions to watch over
def read_log(url):
    with open(url, "r") as f:
        file_content = f.read()
    return file_content

class OutputWatcher:
    def __init__(self, **kwargs):
        self.output = {}
        for message, function in kwargs.items():
            function_output = function()
            self.output.update({(function, message): (function_output, function_output)})


    def update(self):
        for (message, function), (old_output, current_output) in self.output.items():
            new_output = function()
            if current_output != new_output:
                if old_output == new_output:
                    # function is back to the old state
                    self.output.update({(function, message): (current_output, new_output)})
                    message = f"Alter Zustand wieder erreicht!\n{message}"
                    self._send_update(message, new_output)
                else:
                    # function has an unknown state
                    self.output.update({(function, message): (current_output, new_output)})
                    self._send_update(message, new_output)
                return True
            else:
                logging.info("No difference found")
                logging.info(new_output)
                return False



    def _send_update(self, message, output):
        text = f"{message}\n{output}"
        response = send(text)
        logging.info(text)
        logging.info(response)
        logging.info(response.json())


class FileWatcher(OutputWatcher):
    def _send_update(self, descriptor, log):
        inital_comment = f"{descriptor} at {get_time()}"
        response = send_file(log, inital_comment)
        logging.info(log)
        logging.info(response)
        logging.info(response.json())



# utilities
def get_time():
    europe = timezone('Europe/Berlin')
    sa_time = datetime.now(europe)
    return sa_time.strftime('%Y-%m-%d_%H-%M-%S')


def send_file(url, inital_comment):
    return client.files_upload(
                channels='#10_logs',
                text=inital_comment,
                file=url)

def send(text):
    return client.chat_postMessage(
                channel='#10_logs',
                text=text)


def main():
    logs_config = {
        "Spring Dev": lambda: read_log("/root/backend-development_dev-nohup2.out"),
        "Spring Master": lambda: read_log("/root/backend-development-nohup2.out")
    }
    system_status_config = {
        "System Status": web.http_status_code
    }
    LogWatcher = FileWatcher(**logs_config)
    SystemStatusWatcher = OutputWatcher(**system_status_config)

    # magic loop
    while True:
        has_updated = False
        has_updated = LogWatcher.update()
        has_updated = SystemStatusWatcher.update()
        if has_updated:
            time.sleep(600) # 10 min
        else:
            time.sleep(1800) # 30 min