package br.com.financeaiservice.application.usecase;

import br.com.financeaiservice.application.service.LLMResponseService;
import br.com.financeaiservice.application.service.TranscriptionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProcessWhatsAppUseCase {

    private final TranscriptionService transcriptionService;
    private final LLMResponseService llmResponseService;

    public String execute(MultipartFile audio, String text){

        String transcription;

        if (audio != null){
            transcription = transcriptionService.transcribe(audio);
        } else {
            transcription = text;
        }
        return llmResponseService.llmResponse(transcription);
    }
}
