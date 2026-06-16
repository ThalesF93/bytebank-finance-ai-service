package br.com.financeaiservice.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class LLMResponseService {

    private final ChatClient chatClient;

    public String llmResponse(String transcription){
        log.info("Transcription received: {}", transcription);
        return chatClient.prompt()
                .user(transcription)
                .call()
                .content();
    }

}
