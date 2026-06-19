package br.com.financeaiservice.application.service;

import br.com.financeaiservice.infrastructure.context.UserContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class LLMResponseService {

    private final ChatClient chatClient;
    private final VectorStore vectorStore;
    private final UserContext userContext;

    public String llmResponse(String transcription){
        log.info("Transcription received: {}", transcription);

        FilterExpressionBuilder filter = new  FilterExpressionBuilder();
        var expression = filter.eq("customerId", userContext.getUserId()).build();

        List<Document> relevantDocs = vectorStore.similaritySearch(
                SearchRequest.builder()
                        .query(transcription)
                        .topK(10)
                        .similarityThreshold(0.5)
                        .filterExpression(expression)
                        .build()
        );

        String context = relevantDocs.isEmpty() ? "Nenhuma Transação encontrada" : relevantDocs.stream().map(Document::getFormattedContent).collect(Collectors.joining("\n"));

        log.info("RAG context retrieved. docs={}", relevantDocs.size());

        return chatClient.prompt()
                .system(s -> s.param("context", context))
                .advisors(a -> a
                        .param(ChatMemory.CONVERSATION_ID, userContext.getUserId())
                        .param("customerId", userContext.getUserId()))
                .user(transcription)
                .call()
                .content();
    }

}
