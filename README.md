# Glor
Android App for localLLM chatting (Lightweight w/ STTtoTTS)

![5_Screenshot_20240327-140008-imageonline co-merged](https://github.com/ETomberg391/Glor/assets/23483479/a8a8d43d-834d-4057-b2a1-71056a0e342e)

![Screenshot_20240327-140537-imageonline co-merged](https://github.com/ETomberg391/Glor/assets/23483479/e1b700b6-a508-4a2d-a712-261463523aea)

Glor is a lightweight android app to use with your openai API local LLM's. Currently only utilized with a Textgen WebUI server for the customizable openai API.
Pleasenote: The Audio features require /v1/audio/speechAndTextGenerations API endpoint, which will be linked into a seperate Repo specifically for that feature.

The total Server deisgn to work with this currently requires:
- Textgen WebUI server (https://github.com/oobabooga/text-generation-webui)
- WhisperAI extension (whisper_stt in textgen webui GUI Session Tab)
- AllTalk AI /w API running defaults in Standalone mode (https://github.com/erew123/alltalk_tts)
    (Cannot be an extension of TextGen, repeats text too much)
- Custom /v1/audio/speechAndTextGenerations additions to \text-generation-webui\extensions\openai\script.py
    (Repo still Building for this - standby)


Some highlights:

Audio Features (STT to TTS):
good quality recording
Displays original audio + text transcription + response text + response audio
Saves a copy of the recordings locally for viewing (Might be bad depending on storage requirements you have??)
Auto-plays the Response audio for a more fluid experience of conversation.

Text:
Simple showing Original text + Response Text
Has some formatting to split the response text up.
Pressing shift+enter should extend the textbar to type with better formatting (But requires keyboard to enact)
Has some built-in codeblocks for common languages such as python, php, c++

Settings:
Dropdown menu style. Set your settings and pop it back up.
Endpoint setting. Set the IP+Port of your textgenui server (Default: https://10.9.0.3:5000/)
API Key. If none is set, it ignores the value when sending requests. Set if you have one for your server.
Instruction Template. Currently only set to Alpaca, Mistral, and ChatML. More would need to be added manually before building the APK... (Default: Alpaca)
Max Tokens. Default should be 2000. Range from 100 to 4000.
Temperature. Default is 0.7. Range from 0.0 to 1.0
Save button will save the settings. (But instruction template. That needs to be fixed sometime.)
