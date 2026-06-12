package br.com.financeaiservice.infrastructure.config;


import br.com.financeaiservice.application.usecase.PersistOperationToolUseCase;
import br.com.financeaiservice.application.usecase.PersistOperationUseCase;
import br.com.financeaiservice.application.usecase.ProcessAudioUseCase;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.Charset;

@Component
public class ChatClientConfig {

    @Value("classpath:/prompts/openai-function.st")
    private Resource systemPrompt;

    @Bean
    public ChatClient chatClient(OpenAiChatModel openAiChatModel,
                                 PersistOperationToolUseCase tool) throws IOException {


        return ChatClient.builder(openAiChatModel)
                .defaultSystem(systemPrompt.getContentAsString(Charset.defaultCharset()))
                .defaultTools(tool)
                .defaultOptions(OpenAiChatOptions.builder()
                        .temperature(0.0)
                        .model("gpt-4o-mini")
                        .responseFormat(OpenAiChatModel.ResponseFormat.builder().type(OpenAiChatModel.ResponseFormat.Type.TEXT).build()))
                .build();
    }

}
