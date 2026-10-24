package com.bwasiuk.localrag.service;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.rag.advisor.RetrievalAugmentationAdvisor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RagService {
  private final ChatClient chatClient;
  private final VectorSearchService vectorSearchService;
  private final RetrievalAugmentationAdvisor ragAdvisor;

  public String ask(String question) {
    List<Document> documents = vectorSearchService.search(question);
    String context = documents.stream()
        .map(Document::getText)
        .collect(Collectors.joining("\n\n"));

    String prompt = """
        Answer to the question based only on the given context.
        The answer must be given in polish.
        
        CONTEXT:
        %s
        
        QUESTION:
        %s
        """.formatted(context, question);

    return chatClient.prompt()
        .user(prompt)
        .call()
        .content();
  }


  public String askWithRagAdvisor(String question) {
    return chatClient.prompt()
        .advisors(ragAdvisor)
        .user(question)
        .call()
        .content();
  }
}
