import subprocess
import requests


def generate_tts_espeak(text):
    # TODO error handling
    cmd = ["espeak", "-v", "de", text, "-w", "speech.wav"]
    subprocess.run(cmd)


def generate_tts_mary(text):
    # FIXME
    params = (
        ('INPUT_TYPE', 'TEXT'),
        ('OUTPUT_TYPE', 'AUDIO'),
        ('INPUT_TEXT', text),
        ('OUTPUT_TEXT', ''),
        ('effect_Volume_selected', ''),
        ('effect_Volume_parameters', 'amount:2.0;'),
        ('effect_Volume_default', 'Default'),
        ('effect_Volume_help', 'Help'),
        ('effect_TractScaler_selected', ''),
        ('effect_TractScaler_parameters', 'amount:1.5;'),
        ('effect_TractScaler_default', 'Default'),
        ('effect_TractScaler_help', 'Help'),
        ('effect_F0Scale_selected', ''),
        ('effect_F0Scale_parameters', 'f0Scale:2.0;'),
        ('effect_F0Scale_default', 'Default'),
        ('effect_F0Scale_help', 'Help'),
        ('effect_F0Add_selected', ''),
        ('effect_F0Add_parameters', 'f0Add:50.0;'),
        ('effect_F0Add_default', 'Default'),
        ('effect_F0Add_help', 'Help'),
        ('effect_Rate_selected', ''),
        ('effect_Rate_parameters', 'durScale:1.5;'),
        ('effect_Rate_default', 'Default'),
        ('effect_Rate_help', 'Help'),
        ('effect_Robot_selected', ''),
        ('effect_Robot_parameters', 'amount:100.0;'),
        ('effect_Robot_default', 'Default'),
        ('effect_Robot_help', 'Help'),
        ('effect_Whisper_selected', ''),
        ('effect_Whisper_parameters', 'amount:100.0;'),
        ('effect_Whisper_default', 'Default'),
        ('effect_Whisper_help', 'Help'),
        ('effect_Stadium_selected', ''),
        ('effect_Stadium_parameters', 'amount:100.0'),
        ('effect_Stadium_default', 'Default'),
        ('effect_Stadium_help', 'Help'),
        ('effect_Chorus_selected', ''),
        ('effect_Chorus_parameters', 'delay1:466;amp1:0.54;delay2:600;amp2:-0.10;delay3:250;amp3:0.30'),
        ('effect_Chorus_default', 'Default'),
        ('effect_Chorus_help', 'Help'),
        ('effect_FIRFilter_selected', ''),
        ('effect_FIRFilter_parameters', 'type:3;fc1:500.0;fc2:2000.0'),
        ('effect_FIRFilter_default', 'Default'),
        ('effect_FIRFilter_help', 'Help'),
        ('effect_JetPilot_selected', ''),
        ('effect_JetPilot_parameters', ''),
        ('effect_JetPilot_default', 'Default'),
        ('effect_JetPilot_help', 'Help'),
        ('HELP_TEXT', ''),
        ('exampleTexts', ''),
        ('VOICE_SELECTIONS', 'bits1-hsmm de female hmm'),
        ('AUDIO_OUT', 'WAVE_FILE'),
        ('LOCALE', 'de'),
        ('VOICE', 'bits1-hsmm'),
        ('AUDIO', 'WAVE_FILE'),
    )
    mary_url = "http://localhost:59125/process"
    response = requests.post(mary_url, params=params, stream=True)
    response.raise_for_status()
    with open('mary.wav', 'wb') as out_file:
        out_file.write(response.content)
        # shutil.copyfileobj(response.raw, out_file)
    del response
