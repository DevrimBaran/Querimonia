import slack_bot
import watcher
from threading import Thread


if __name__ == "__main__":
    threads = [Thread(target=watcher.main)]
    for thread in threads:
        thread.daemon = True
        thread.start()
    slack_bot.main()
    for thread in threads:
        thread.join()
