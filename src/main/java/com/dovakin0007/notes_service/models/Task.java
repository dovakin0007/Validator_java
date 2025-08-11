package com.dovakin0007.notes_service.models;

import java.util.List;

public record Task(
        String id,
        String projectId,
        User createdBy,
        String title,
        String description,
        TaskStatus status,
        TaskPriority priority,
        String dueDate,
        List<User> assignees,
        List<TaskComment> comments,
        String createdAt,
        String updatedAt) {
}
