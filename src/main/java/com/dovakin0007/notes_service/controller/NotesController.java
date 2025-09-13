package com.dovakin0007.notes_service.controller;

import com.dovakin0007.notes_service.service.GrpcNotesClient;
import com.dovakin0007.notes_service.service.GrpcUserClient;
import org.springframework.stereotype.Controller;

@Controller
public class NotesController {
    private final GrpcNotesClient notes_service;
    public NotesController(GrpcNotesClient grpcUserService) {
        this.notes_service = grpcUserService;
    }
}
