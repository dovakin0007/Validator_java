package com.dovakin0007.notes_service.models;

import java.util.List;

public record Project(
    String id,
    User owner,
    String name,
    String description,
    List<ProjectMember> members,
    List<Task> tasks,
    List<Note> notes,
    User createdBy,
    String createdAt,
    String updatedAt,
    ProjectStatus status
) {}







