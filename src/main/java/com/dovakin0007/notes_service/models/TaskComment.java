package com.dovakin0007.notes_service.models;

public record TaskComment(
    String id,
    String taskId,
    User author,
    String content,
    String createdAt,
    String updatedAt
) {}