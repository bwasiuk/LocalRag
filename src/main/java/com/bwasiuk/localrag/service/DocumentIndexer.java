package com.bwasiuk.localrag.service;

import com.bwasiuk.localrag.model.IndexingResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.core.io.FileSystemResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class DocumentIndexer {
  private final VectorStore vectorStore;
  private final JdbcTemplate jdbcTemplate;

  public IndexingResult indexDocument(String filePath) {
    validateFile(filePath);

    if(documentAlreadyIndexed(filePath)) {
      throw new IllegalArgumentException("Document has been already indexed.");
    }

    long startTime = System.currentTimeMillis();

    var reader = new PagePdfDocumentReader(
        new FileSystemResource(filePath)
    );
    List<Document> pages = reader.get();

    enrichMetadata(pages, filePath);

    var splitter = TokenTextSplitter.builder()
        .withChunkSize(200)
        .withMinChunkLengthToEmbed(10)
        .withMinChunkSizeChars(50)
        .build();
    List<Document> chunks = splitter.apply(pages);
    vectorStore.accept(chunks);

    long duration = System.currentTimeMillis() - startTime;

    return new IndexingResult(
        Path.of(filePath).getFileName().toString(),
        pages.size(),
        chunks.size(),
        duration
    );
  }

  private void enrichMetadata(List<Document> pages, String filePath) {
    String fileName = Path.of(filePath).getFileName().toString();

    pages.forEach(document -> {
      document.getMetadata().put("source", fileName);
      document.getMetadata().put("path", filePath);
      document.getMetadata().put("documentId", filePath);
    });
  }

  private void validateFile(String filePath) {
    Path path = Path.of(filePath);

    if(!Files.exists(path)) {
      throw new IllegalArgumentException("File doesn't exist.");
    }

    if(!filePath.endsWith(".pdf")) {
      throw new IllegalArgumentException("Only PDF files are supported.");
    }
  }

  private boolean documentAlreadyIndexed(String documentId) {
    Integer count = jdbcTemplate.queryForObject(
        """
            SELECT COUNT(*)
            FROM public.vector_store
            WHERE metadata ->> 'documentId' = ?
            """,
        Integer.class,
        documentId
    );
    return count != null && count > 0;
  }
}
