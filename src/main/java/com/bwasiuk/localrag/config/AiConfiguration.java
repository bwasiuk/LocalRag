package com.bwasiuk.localrag.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.rag.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.rag.generation.augmentation.ContextualQueryAugmenter;
import org.springframework.ai.rag.generation.augmentation.QueryAugmenter;
import org.springframework.ai.rag.retrieval.search.VectorStoreDocumentRetriever;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AiConfiguration {
  @Bean
  ChatClient chatClient(ChatModel chatModel) {
    return ChatClient.create(chatModel);
  }

  @Bean
  public RetrievalAugmentationAdvisor ragAdvisor(VectorStore vectorStore) {

    var retriever = VectorStoreDocumentRetriever.builder()
        .vectorStore(vectorStore)
        .similarityThreshold(0.5)
        .topK(5)
        .build();

    var queryAugmenter = ContextualQueryAugmenter.builder()
        .allowEmptyContext(false)
        .build();

    return RetrievalAugmentationAdvisor.builder()
        .documentRetriever(retriever)
        .queryAugmenter(queryAugmenter)
        .build();
  }
}
