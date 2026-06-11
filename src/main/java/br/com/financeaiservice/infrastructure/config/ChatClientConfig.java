package br.com.financeaiservice.infrastructure.config;


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
                                 PersistOperationUseCase persistOperationUseCase) throws IOException {

        var options = OpenAiChatOptions.builder()
                .model("gpt-4o-mini")
                .temperature(0.0)
                .responseFormat(OpenAiChatModel.ResponseFormat.builder().type(OpenAiChatModel.ResponseFormat.Type.TEXT).build())
                .build();

        return ChatClient.builder(openAiChatModel)
               // .defaultOptions(options)
                .defaultSystem(systemPrompt.getContentAsString(Charset.defaultCharset()))
                .defaultTools(persistOperationUseCase)
                .build();
    }

}
