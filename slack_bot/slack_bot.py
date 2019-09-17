import pdb
import os
import slack
import redmine
import actions


OWN_ID = "ULJUHV2BZ"


@slack.RTMClient.run_on(event='message')
def message_handler(**payload):
    # TODO integrate BUTTON support, with integrated Flask Server
    # setup
    data = payload['data']
    web_client = payload['web_client']
    channel_id = data['channel']
    wait_message = "Ok, ich hole mir die neuesten Daten"
    wait_message_simple = "Ok, einen Moment bitte"

    def send(text, custom_channel=None):
        current_channel_id = channel_id
        if custom_channel != None:
            current_channel_id = custom_channel
        web_client.chat_postMessage(
            channel=current_channel_id,
            text=text
            )
    def action_send(function):
        send(wait_message_simple)
        result = function()
        if len(result) > 0:
            send(result)
        else:
            send("Aktion durchgeführt")


    if "user" in data and data["user"] != OWN_ID:
        user = data['user']
        text_input = data['text']
        # long list of if/elif :/
        # TODO use a dict instead
        if "Kommentare" in text_input:
            send(wait_message)
            message = redmine.get_warn_message()
            send(message, "01_entwicklungsteam")
            send("Ich habe die Nachricht in 01_entwicklungsteam geschickt")
        elif "Status" in text_input:
            action_send(actions.web.http_status_code)
        elif "Präsentation" in text_input:
            send(wait_message)
            redmine.create_presentation()
            # TODO def send_file
            web_client.files_upload(
                channels=channel_id,
                file="present.pdf",
                title="Präsentation"
                )
        elif "Sinn" in text_input:
            send("42")
        elif "hi" in text_input:
            send(f"hi <@{user}>!\n")
        # new
        elif "Reset_dev" in text_input:
            action_send(actions.web.reset_dev)
        elif "Reset" in text_input:
            action_send(actions.web.reset)
        elif "df" in text_input:
            action_send(actions.system.df)
        elif "free" in text_input:
            action_send(actions.system.free)
        elif "restart_spring_dev" in text_input:
            action_send(actions.system.restart_spring_dev)
        elif "restart_spring" in text_input:
            action_send(actions.system.restart_spring)
        elif "restart_flask" in text_input:
            action_send(actions.system.restart_flask)
        elif "clear_database_dev" in text_input:
            action_send(actions.system.clear_database_dev)
        elif "clear_database" in text_input:
            action_send(actions.system.clear_database)
        elif "help" in text_input:
            # TODO use dict.keys() instead
            send("Kommentare, Status, Präsentation, Sinn, hi, Reset_dev, \
Reset, df, free, restart_spring_dev, restart_spring, restart_flask, \
clear_database_dev, clear_database, help")
        else: 
            send("Das habe ich leider nicht verstanden")


def main():
    slack_token = os.environ["SLACK_API_TOKEN"]
    rtm_client = slack.RTMClient(token=slack_token)
    rtm_client.start()
