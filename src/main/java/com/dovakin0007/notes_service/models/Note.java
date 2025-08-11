package com.dovakin0007.notes_service.models;

import java.util.List;

public record Note(
    String id,
    String projectId,
    User author,
    String title,
    String content,
    boolean isPinned,
    List<String> tags,
    List<NoteRevision> revisions,
    List<Attachment> attachments,
    String createdAt,
    String updatedAt
) {}

