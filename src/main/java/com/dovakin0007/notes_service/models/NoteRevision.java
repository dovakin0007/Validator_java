package com.dovakin0007.notes_service.models;

public record NoteRevision(
        String id,
        String content,
        User editor,
        String editedAt
) {
}