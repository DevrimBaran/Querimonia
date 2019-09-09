#!/usr/bin/env python
# -*- coding: utf-8 -*-
"""
telegram QuerimoniaBot
"""
from queue import Queue
from threading import Thread

from flask import Flask, Response, request

import logging
import requests
from requests.auth import HTTPBasicAuth
from telegram.ext import Updater, CommandHandler, MessageHandler, Filters
from telegram import Bot
import utilities

app = Flask(__name__)
logging.basicConfig(format='%(asctime)s - %(name)s - %(levelname)s - %(message)s', level=logging.INFO)
logger = logging.getLogger(__name__)


##### telegram functions #####
def start(update, context):
    """Send a message when the command /start is issued."""
    update.message.reply_text('Bitte schreiben Sie ihre Beschwerde')


def help(update, context):
    """Send a message when the command /help is issued."""
    update.message.reply_text('Help!')


def complaint_handler(update, context):
    """Handle complaints, trigger without / command"""
    user_id = update.message.from_user.id
    user_message = update.message.text
    complaint_sender_queue.put((user_id, user_message))


def error(update, context):
    """Log Errors caused by Updates."""
    logger.warning('Update "%s" caused error "%s"', update, context.error)


def start_telegram_bot():
    """Start the bot."""
    updater = Updater("966671316:AAHTZR-6bUSjPHL4cBmZhJIaIKCniKH0fA8", use_context=True)
    dp = updater.dispatcher
    dp.add_handler(CommandHandler("start", start))
    dp.add_handler(CommandHandler("help", help))
    dp.add_handler(MessageHandler(Filters.text, complaint_handler))
    # log all errors
    dp.add_error_handler(error)
    # Start the Bot
    updater.start_polling()
    updater.idle()


##### threads #####
def complaint_sender():
    """sends complaint to server in a Thread"""
    credentials = HTTPBasicAuth('admin', 'QuerimoniaPass2019')
    q = complaint_sender_queue
    while True:
        user_id, user_message = q.get()
        r = requests.post("https://querimonia.iao.fraunhofer.de/dev/api/complaints/import", json={"text": user_message}, auth=credentials)
        complaint_id = int(r.json()["id"])
        sendBot.send_message(user_id, f"Vielen Dank für Ihre Nachricht. Ihr Anliegen wird unter der ID {str(complaint_id)} bearbeitet.")
        # save for later
        complaint_data.update({complaint_id: user_id})
        q.task_done()


def start_flask():
    app.run(debug=False, port=59127)


##### flask server #####
@app.route("/telegram/notify/", methods=["POST"])
def complaint_receiver():
    content = request.json
    complaint_id = content["id"]
    complaint_message = content["message"]
    # get user id and send answer
    user_id = complaint_data[complaint_id]
    del complaint_data[complaint_id]
    answer_complaint(user_id, complaint_message)
    resp = Response(status=200, mimetype="application/json")
    return resp


# utility
def answer_complaint(user_id, message):
    # TODO get audio inline, you could get the wrong audio
    # file for another user
    utilities.generate_tts_mary(message)
    sendBot.send_message(user_id, message)
    sendBot.send_voice(user_id, voice=open("mary.wav", "rb"))


# startup
complaint_sender_queue = Queue()
# poor mans database
complaint_data = {}
sendBot = Bot("966671316:AAHTZR-6bUSjPHL4cBmZhJIaIKCniKH0fA8")


if __name__ == "__main__":
    # TODO seperate functions into modules
    # TODO (low) replace threads with async
    for thread in [complaint_sender, start_flask]:
        worker_thread = Thread(target=thread)
        worker_thread.daemon = True
        worker_thread.start()
    start_telegram_bot()
