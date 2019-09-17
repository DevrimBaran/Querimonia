import slack_bot
import watcher
from threading import Thread


if __name__ == "__main__":
    for thread in [watcher.main, slack_bot.main]:
        worker_thread = Thread(target=thread)
        worker_thread.daemon = True
        worker_thread.start()
