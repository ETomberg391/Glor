# Glor
Android App for localLLM chatting (Lightweight w/ STTtoTTS)

![successfulApp](https://github.com/ETomberg391/Glor/assets/23483479/7b8e3a89-f958-42ca-9411-c993012d9bf9) ![issuefurther](https://github.com/ETomberg391/Glor/assets/23483479/2b920d78-3fef-437b-93f2-30ba2a30088f)

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
