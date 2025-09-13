package com.dovakin0007.notes_service.service;

import com.dovakin0007.notes_service.models.NoteModels;
import com.dovakin0007.notesservice.CreateNoteRequest;
import com.dovakin0007.notesservice.Note;
import com.dovakin0007.notesservice.NoteResponse;
import com.dovakin0007.notesservice.NoteServiceGrpc;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

@Service
public class GrpcNotesClient {
    @GrpcClient("notes-service")
    private NoteServiceGrpc.NoteServiceFutureStub noteNonBlockingStub;

    private final Executor mappingExecutor;

    public GrpcNotesClient(@Qualifier("grpcMappingExecutor") Executor mappingExecutor) {
        this.mappingExecutor = mappingExecutor;
    }


    private static <T> CompletableFuture<T> toCompletableFuture(ListenableFuture<T> lf) {
        CompletableFuture<T> cf = new CompletableFuture<>();
        lf.addListener(() -> {
            try {
                cf.complete(lf.get());
            } catch (ExecutionException ee) {
                cf.completeExceptionally(ee.getCause() == null ? ee : ee.getCause());
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
                cf.completeExceptionally(ie);
            } catch (Exception e) {
                cf.completeExceptionally(e);
            }
        }, MoreExecutors.directExecutor());
        return cf;
    }


    private NoteModels.Note mapProtoNoteToDomain(com.dovakin0007.notesservice.Note p) {
        if (p == null) return null;

        List<String> tags = new ArrayList<>(p.getTagsList());
        List<NoteModels.NoteRevision> revisions = p.getRevisionsList().stream().map(this::mapProtoRevisionToDomain).collect(Collectors.toList());
        List<NoteModels.Attachment> attachments = p.getAttachmentsList().stream().map(this::mapProtoAttachmentToDomain).collect(Collectors.toList());

        Instant createdAt = p.hasCreatedAt()
                ? Instant.ofEpochSecond(p.getCreatedAt().getSeconds(), p.getCreatedAt().getNanos())
                : Instant.EPOCH; // or Instant.now()
        Instant updatedAt = p.hasUpdatedAt()
                ? Instant.ofEpochSecond(p.getUpdatedAt().getSeconds(), p.getUpdatedAt().getNanos())
                : createdAt; // default updatedAt to createdAt if missing
        NoteModels.ActorRef author = p.hasAuthor() ? mapProtoActorRefToDomain(p.getAuthor()) : null;

        return new NoteModels.Note(
                p.getId(),
                p.hasProjectId() && !p.getProjectId().isEmpty() ? p.getProjectId() : null,
                author,
                p.getTitle(),
                p.hasContent() && !p.getContent().isEmpty() ? p.getContent() : null,
                p.getIsPinned(),
                tags,
                revisions,
                attachments,
                createdAt,
                updatedAt
        );
    }

    private NoteModels.NoteRevision mapProtoRevisionToDomain(com.dovakin0007.notesservice.NoteRevision r) {
        Instant editedAt = r.hasEditedAt() ? Instant.ofEpochSecond(r.getEditedAt().getSeconds(), r.getEditedAt().getNanos()) : Instant.EPOCH;
        return new NoteModels.NoteRevision(r.getId(), r.getTitle(), r.getContent(), mapProtoActorRefToDomain(r.getEditor()), editedAt);
    }

    private NoteModels.Attachment mapProtoAttachmentToDomain(com.dovakin0007.notesservice.Attachment a) {
        Instant uploaded = a.hasUploadedAt() ? Instant.ofEpochSecond(a.getUploadedAt().getSeconds(), a.getUploadedAt().getNanos()) : Instant.EPOCH;
        return new NoteModels.Attachment(a.getId(), a.getUrl(), a.getFileName(), a.getFileType(), uploaded, a.hasSha256() ? a.getSha256() : null, a.hasSizeBytes() ? a.getSizeBytes() : null);
    }

    private NoteModels.Note mapNoteResponseToDomainNote(com.dovakin0007.notesservice.NoteResponse n) {
        Note note = n.getNote();
        return mapProtoNoteToDomain(note);
    }

    private NoteModels.ActorRef mapProtoActorRefToDomain(com.dovakin0007.notesservice.ActorRef a) {
        if (a == null) return null;
        return new NoteModels.ActorRef(a.getId(), a.hasDisplayName() && !a.getDisplayName().isEmpty() ? a.getDisplayName() : null, a.hasAvatarUrl() && !a.getAvatarUrl().isEmpty() ? a.getAvatarUrl() : null);
    }


    private com.dovakin0007.notesservice.ActorRef fromDomainActorRef(NoteModels.ActorRef d) {
        if (d == null) return com.dovakin0007.notesservice.ActorRef.getDefaultInstance();
        com.dovakin0007.notesservice.ActorRef.Builder b = com.dovakin0007.notesservice.ActorRef.newBuilder().setId(d.id());
        if (d.displayName() != null) b.setDisplayName(d.displayName());
        if (d.avatarUrl() != null) b.setAvatarUrl(d.avatarUrl());
        return b.build();
    }



    private com.dovakin0007.notesservice.Attachment fromDomainAttachment(NoteModels.Attachment d) {
        if (d == null) return com.dovakin0007.notesservice.Attachment.getDefaultInstance();
        com.dovakin0007.notesservice.Attachment.Builder b = com.dovakin0007.notesservice.Attachment.newBuilder()
                .setId(d.id())
                .setUrl(d.url())
                .setFileName(d.fileName())
                .setFileType(d.fileType());
        if (d.uploadedAt() != null) {
            Instant inst = Instant.parse(d.uploadedAt().toString());
            b.setUploadedAt(com.google.protobuf.Timestamp.newBuilder().setSeconds(inst.getEpochSecond()).setNanos(inst.getNano()).build());
        }
        if (d.sha256() != null) b.setSha256(d.sha256());
        if (d.sizeBytes() != null) b.setSizeBytes(d.sizeBytes());
        return b.build();
    }

    public CompletableFuture<NoteModels.Note> createNote(NoteModels.CreateNoteRequest reqDomain) {
        CreateNoteRequest.Builder b = CreateNoteRequest.newBuilder()
                .setTitle(reqDomain.title())
                .setIsPinned(reqDomain.isPinned());

        if (reqDomain.projectId() != null) b.setProjectId(reqDomain.projectId());
        if (reqDomain.content() != null) b.setContent(reqDomain.content());
        if (reqDomain.idempotencyKey() != null) b.setIdempotencyKey(reqDomain.idempotencyKey());
        if (reqDomain.tags() != null) reqDomain.tags().forEach(b::addTags);
        if (reqDomain.attachments() != null) reqDomain.attachments().forEach(att -> b.addAttachments(fromDomainAttachment(att)));
        if (reqDomain.author() != null) b.setAuthor(fromDomainActorRef(reqDomain.author()));

        ListenableFuture<NoteResponse> lf = noteNonBlockingStub.createNote(b.build());
        return toCompletableFuture(lf).thenApplyAsync(this::mapNoteResponseToDomainNote, mappingExecutor);
    }
}
