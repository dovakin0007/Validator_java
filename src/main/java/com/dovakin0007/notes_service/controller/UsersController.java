package com.dovakin0007.notes_service.controller;

import java.util.List;
import java.util.concurrent.CompletionStage;

import com.dovakin0007.notes_service.service.GrpcNotesClient;
import com.dovakin0007.notes_service.service.GrpcUserClient;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;

import com.dovakin0007.notes_service.models.User;
// import com.dovakin0007.notes_service.service.UserService;

@Controller
public class UsersController {

    // private final UserService service;

    private final GrpcUserClient user_service;



    // public UsersController(UserService service, GrpcUserService grpcUserService) {
        public UsersController(GrpcUserClient grpcUserService) {
        // this.service = service;
        this.user_service = grpcUserService;

    }

    @QueryMapping
    @PreAuthorize("hasRole('USER')")
    public List<User> listUsers() {
        return user_service.listAllUsers();
    }

    @MutationMapping
     @PreAuthorize("hasRole('ADMIN') || hasRole('USER')")
    public CompletionStage<User> createUser(@Argument String name, @Argument String email) {
        return user_service.createUser(name, email).thenApply(u -> u);
    }

    @QueryMapping
    @PreAuthorize("hasRole('ADMIN', 'USER')")
    public User getSpecificUser(@Argument String id) {
        return user_service.getSpecificUser(id);
    }

}
