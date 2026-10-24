package com.bwasiuk.localrag.model;

public record IndexingResult(
    String fileName,
    int pageCount,
    int chunkCount,
    long durationMs
) {}
