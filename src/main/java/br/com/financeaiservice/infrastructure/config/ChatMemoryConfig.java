package br.com.financeaiservice.infrastructure.config;

import org.springframework.ai.chat.memory.ChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class ChatMemoryConfig {

    @Bean
    public MessageWindowChatMemory chatMemory(ChatMemoryRepository repository){
            return MessageWindowChatMemory.builder()
                    .maxMessages(20)
                    .chatMemoryRepository(repository)
                    .build();
    }
}
