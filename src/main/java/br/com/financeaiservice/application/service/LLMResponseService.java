package br.com.financeaiservice.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LLMResponseService {

    private final ChatClient chatClient;

    public String llmResponse(String transcription){
        return chatClient.prompt().user(transcription).call().content();
    }

}
