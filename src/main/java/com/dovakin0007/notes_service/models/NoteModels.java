package com.dovakin0007.notes_service.models;

import java.util.List;

public class NoteModels {
    public record ActorRef(
            String id,
            String displayName,
            String avatarUrl
    ) {
    }

    public record NoteRevision(
            String id,
            String title,
            String content,
            ActorRef editor,
            java.time.Instant editedAt
    ) {
    }

    public record Attachment(
            String id,
            String url,
            String fileName,
            String fileType,
            java.time.Instant uploadedAt,
            String sha256,
            Long sizeBytes
    ) {
    }

    public record Note(
            String id,
            String projectId,
            ActorRef author,
            String title,
            String content,
            boolean isPinned,
            java.util.List<String> tags,
            java.util.List<NoteRevision> revisions,
            java.util.List<Attachment> attachments,
            java.time.Instant createdAt,
            java.time.Instant updatedAt
    ) {
    }

    // Optionally: request/response records here too
    public record GetNoteRequest(String id, boolean includeRevisions, boolean includeAttachments) {
    }

    public record NoteResponse(Note note) {
    }

    public record CreateNoteRequest(
            String projectId,
            String title,
            String content,
            boolean isPinned,
            List<String> tags,
            List<Attachment> attachments,
            ActorRef author,
            String idempotencyKey
    ) {
    }
}
