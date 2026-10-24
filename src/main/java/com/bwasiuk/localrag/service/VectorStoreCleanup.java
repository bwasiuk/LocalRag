package com.bwasiuk.localrag.service;

import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class VectorStoreCleanup {
  private final JdbcTemplate jdbcTemplate;

  @PreDestroy
  public void cleanup() {
    System.out.println();
    System.out.println("Cleaning up vector store...");

    this.jdbcTemplate.execute("DELETE FROM public.vector_store");

    System.out.println("Vector store has been cleaned up.");
  }
}
