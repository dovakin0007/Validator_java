package com.dovakin0007.notes_service.models;

public record User(
    String id,
    String name,
    String email,
    String avatarUrl,
    String createdAt,
    String updatedAt,
    String bio
) {}
