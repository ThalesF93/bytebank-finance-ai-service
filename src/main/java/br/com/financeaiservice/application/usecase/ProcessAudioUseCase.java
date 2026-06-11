package br.com.financeaiservice.application.usecase;

import br.com.financeaiservice.application.service.LLMResponseService;
import br.com.financeaiservice.application.service.TextToSpeechService;
import br.com.financeaiservice.application.service.TranscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class ProcessAudioUseCase {

    private final LLMResponseService llmResponseService;
    private final TextToSpeechService textToSpeechService;
    private final TranscriptionService transcriptionService;

    @Tool(name = "", description = "Persiste uma nova transação financeira")
    public byte[] execute(MultipartFile file){


        var transcription = transcriptionService.transcribe(file);

        var llmResponse = llmResponseService.llmResponse(transcription);

        return textToSpeechService.textToSpeech(llmResponse);
    }
}
