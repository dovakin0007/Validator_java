package com.dovakin0007.notes_service.models;

public record Attachment(
    String id,
    String url,
    String fileName,
    String fileType,
    String uploadedAt
) {}