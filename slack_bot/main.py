import slack_bot
import watcher
from threading import Thread


if __name__ == "__main__":
    threads = [watcher.main, slack_bot.main]
    for thread in threads:
        worker_thread = Thread(target=thread)
        worker_thread.daemon = True
        worker_thread.start()

    for thread in threads:
        thread.join()
