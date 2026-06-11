package br.com.financeaiservice.infrastructure.config;

import br.com.ai_budgeting.application.ListTransactionsByCategoryUseCase;
import br.com.ai_budgeting.application.PersistTransactionUseCase;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.ResponseFormat;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.Charset;

@Component
public class ChatClientConfig {

    @Value("classpath:/prompts/system-message.st")
    private Resource systemPrompt;

    @Bean
    public ChatClient chatClient(OpenAiChatModel openAiChatModel,
                                 PersistTransactionUseCase persistTransactionUseCase,
                                 ListTransactionsByCategoryUseCase listTransactionsByCategoryUseCase) throws IOException {

        var options = OpenAiChatOptions.builder()
                .model("gpt-4o-mini")
                .temperature(0.0)
                .responseFormat(ResponseFormat.builder().type(ResponseFormat.Type.TEXT).build())
                .build();

        return ChatClient.builder(openAiChatModel)
                .defaultOptions(options)
                .defaultSystem(systemPrompt.getContentAsString(Charset.defaultCharset()))
                .defaultTools(persistTransactionUseCase, listTransactionsByCategoryUseCase)
                .build();
    }

//    @Bean
//    ChatClient chatClient(ChatClient.Builder builder){
//        return builder.build();
//    }
}
