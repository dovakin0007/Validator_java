package com.dovakin0007.notes_service.models;

public record ProjectMember(
    String id,
    User user,
    ProjectRole role,
    String addedAt
) {}
