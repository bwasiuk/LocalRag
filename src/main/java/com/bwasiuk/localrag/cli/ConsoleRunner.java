package com.bwasiuk.localrag.cli;

import com.bwasiuk.localrag.model.IndexingResult;
import com.bwasiuk.localrag.service.DocumentIndexer;
import com.bwasiuk.localrag.service.RagService;
import com.bwasiuk.localrag.service.VectorSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.document.Document;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Scanner;

@Component
@RequiredArgsConstructor
public class ConsoleRunner implements CommandLineRunner {
  private final DocumentIndexer documentIndexer;
  private final RagService ragService;
  private final VectorSearchService vectorSearchService;

  @Override
  public void run(String... args) throws Exception {
    Scanner scanner = new Scanner(System.in);

    while(true) {
      printMenu();
      String selection = scanner.nextLine();
      switch (selection) {
        case "1" -> loadDocument(scanner);
        case "2" -> askManualRag(scanner);
        case "3" -> askSpringAiAdvisor(scanner);
        case "4" -> showChunks(scanner);
        case "5" -> exit();
        default -> System.out.println(("This option is not supported."));
      }
    }
  }

  private void printMenu() {
    System.out.println();
    System.out.println("==============================");
    System.out.println("      Spring AI RAG Demo      ");
    System.out.println("==============================");
    System.out.println("1. Load document");
    System.out.println("2. Ask (Manual RAG)");
    System.out.println("3. Ask (Spring AI Advisor)");
    System.out.println("4. Show chunks");
    System.out.println("5. Exit");
    System.out.println();
    System.out.println("Your choice: ");
  }

  private void loadDocument(Scanner scanner) {
    System.out.println();
    System.out.println("PDF file full path: ");
    String path = scanner.nextLine();

    try {
      printIndexingResult(documentIndexer.indexDocument(path));

    } catch (Exception e) {
      System.out.println();
      System.out.println("Error: " + e.getMessage());
    }
  }

  private void printIndexingResult(IndexingResult indexingResult) {
    System.out.printf("Document: %s%n", indexingResult.fileName());
    System.out.printf("Pages: %d%n", indexingResult.pageCount());
    System.out.printf("Chunks: %d%n", indexingResult.chunkCount());
    System.out.printf("Duration: %d ms%n", indexingResult.chunkCount());
  }

  private void askManualRag(Scanner scanner) {
    System.out.println();
    System.out.println("Question: ");
    String question = scanner.nextLine();

    try {
      String answer = ragService.ask(question);
      System.out.println();
      System.out.println("Manual RAG answer: " + answer);
    } catch (Exception e) {
      System.out.println();
      System.out.println("Error: " + e.getMessage());
    }
  }

  private void askSpringAiAdvisor(Scanner scanner) {
    System.out.println();
    System.out.println("Question: ");
    String question = scanner.nextLine();

    try {
      String answer = ragService.askWithRagAdvisor(question);
      System.out.println();
      System.out.println("Answer with Spring AI advisor: " + answer);
    } catch (Exception e) {
      System.out.println();
      System.out.println("Error: " + e.getMessage());
    }
  }

  private void showChunks(Scanner scanner) {
    System.out.println("Query: ");
    String query = scanner.nextLine();

    List<Document> results = vectorSearchService.search(query);

    System.out.println();
    System.out.printf("Found %d matching fragments.%n", results.size());

    if (results.isEmpty()) {
      return;
    }

    int counter = 1;
    for(Document document : results) {
      System.out.println();
      System.out.println("==========");
      System.out.println("Chunk #" + counter);
      System.out.println("==========");
      document.getMetadata().forEach(
          (key, value) -> {
            System.out.printf("%s = %s%n", key, value);
          }
      );
      System.out.println("-----------");

      String text = document.getText();

      if(text.length() > 1000) {
        System.out.println(text.substring(0, 1000) + "\n...");
      }
      else {
        System.out.println(text);
      }
      counter++;
    }
  }

  private void exit() {
    System.out.println();
    System.out.println("Closing application...");
    System.exit(0);
  }
}
