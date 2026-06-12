package br.com.financeaiservice.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.audio.transcription.AudioTranscriptionPrompt;
import org.springframework.ai.openai.OpenAiAudioTranscriptionModel;
import org.springframework.ai.openai.OpenAiAudioTranscriptionOptions;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
@Slf4j
public class TranscriptionService {

    private final OpenAiAudioTranscriptionModel transcriptionModel;

    public String transcribe(MultipartFile file){

        log.info("AudioFile");
        OpenAiAudioTranscriptionOptions options = OpenAiAudioTranscriptionOptions.builder()
                .language("pt")
                .temperature(0f)
                .build();

        Resource audioResource;
        try {
            audioResource = new ByteArrayResource(file.getBytes()) {
                @Override
                public String getFilename() {
                    return "audio.mp3";
                }
            };
        } catch (IOException e) {
            throw new RuntimeException("Erro ao ler o arquivo de áudio", e);
        }


        AudioTranscriptionPrompt prompt = new AudioTranscriptionPrompt(
                audioResource,
                options
        );

        return transcriptionModel.call(prompt).getResult().getOutput();
    }

}
