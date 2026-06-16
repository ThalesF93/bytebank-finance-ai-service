package br.com.financeaiservice.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.audio.tts.TextToSpeechPrompt;
import org.springframework.ai.openai.OpenAiAudioSpeechModel;
import org.springframework.ai.openai.OpenAiAudioSpeechOptions;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
@Slf4j
public class TextToSpeechService {

    private final OpenAiAudioSpeechModel speechModel;

    public byte[] textToSpeech(String text){
        log.info("Answer received: {}, Now turning into voice message", text);

        OpenAiAudioSpeechOptions options = OpenAiAudioSpeechOptions.builder()
                .voice(OpenAiAudioSpeechOptions.Voice.NOVA)
                .responseFormat(OpenAiAudioSpeechOptions.AudioResponseFormat.MP3)
                .speed(1.0D)
                .build();

        TextToSpeechPrompt prompt = new TextToSpeechPrompt(text, options);
        return speechModel.call(prompt).getResult().getOutput();
    }
}
