package com.bwasiuk.localrag.service;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VectorSearchService {
  private final VectorStore vectorStore;

  public List<Document> search(String question) {
    return this.vectorStore.similaritySearch(
        SearchRequest.builder()
            .query(question)
            .topK(5)
            .build()
    );
  }
}
