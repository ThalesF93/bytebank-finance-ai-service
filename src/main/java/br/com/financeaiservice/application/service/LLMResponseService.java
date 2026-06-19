package br.com.financeaiservice.application.service;

import br.com.financeaiservice.infrastructure.context.UserContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
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

        List<Document> relevantDocs = vectorStore.similaritySearch(
                SearchRequest.builder()
                        .query(transcription)
                        .topK(20)
                        .similarityThreshold(0.3)
                        .filterExpression("customerId == '" + userContext.getUserId() +"'")
                        .build()
        );

        String context = relevantDocs.isEmpty() ? "Nenhuma Transação encontrada" : relevantDocs.stream().map(Document::getFormattedContent).collect(Collectors.joining("\n"));

        log.info("RAG context retrieved. docs={}", relevantDocs.size());

        String systemPrompt = """
            Você é um assistente financeiro pessoal do ByteBank.
            Responda sempre em português.
            
            Você tem acesso a duas capacidades:
            
            1. REGISTRAR transações: use a tool disponível quando o usuário
               quiser registrar um gasto, depósito ou transferência.
            
            2. CONSULTAR transações: quando o usuário fizer uma pergunta sobre
               seus gastos, use o contexto abaixo para responder.
               Se não houver contexto suficiente, diga que não encontrou informações.
            
            Transações relevantes do usuário:
            %s
            """.formatted(context);

        return chatClient.prompt()
                .system(systemPrompt)
                .user(transcription)
                .call()
                .content();
    }

}
