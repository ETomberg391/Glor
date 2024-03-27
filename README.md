# Glor
Android App for localLLM chatting (Lightweight w/ STTtoTTS)

![Screenshot_20240327-135924](https://github.com/ETomberg391/Glor/assets/23483479/ca6e6a9e-a43a-4847-bd6a-2d287a687016)![Screenshot_20240327-135945](https://github.com/ETomberg391/Glor/assets/23483479/62aa186c-6659-4838-9b4d-e57425526ae6)![Screenshot_20240327-140037](https://github.com/ETomberg391/Glor/assets/23483479/cc58a81b-a68d-4418-9231-489860d2ad17)![Screenshot_20240327-140052](https://github.com/ETomberg391/Glor/assets/23483479/77dbd0d6-d521-45c5-b40d-19533df0d8f2)![Screenshot_20240327-140008](https://github.com/ETomberg391/Glor/assets/23483479/bfd19d8f-a8a2-4926-8170-c50b31ec75e9)

![Screenshot_20240327-140528](https://github.com/ETomberg391/Glor/assets/23483479/f47521e7-5e79-422a-b18e-2f8f02fc0f17)
![Screenshot_20240327-140537](https://github.com/ETomberg391/Glor/assets/23483479/bf9eb104-2dd5-4817-90b5-59804f5af345)




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
