package com.dovakin0007.notes_service.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.TimeoutException;

import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;

import com.dovakin0007.notes_service.models.User;
import com.dovakin0007.notes_service.service.GrpcUserService;
// import com.dovakin0007.notes_service.service.UserService;

@Controller
public class UsersController {

    // private final UserService service;

    private final GrpcUserService service;

    // public UsersController(UserService service, GrpcUserService grpcUserService) {
        public UsersController(GrpcUserService grpcUserService) {
        // this.service = service;
        this.service = grpcUserService;
    }



    @QueryMapping
    @PreAuthorize("hasRole('USER')")
    public List<User> listUsers() {
        return service.listAllUsers();
        // return service.listAllUser().thenCompose(user -> {
        //     List<User> users = user.orElseGet(() -> new ArrayList<>());
        //     return CompletableFuture.completedFuture(users);
        // });
    }

    @MutationMapping
     @PreAuthorize("hasRole('ADMIN') || hasRole('USER')")
    public CompletionStage<User> createUser(@Argument String name, @Argument String email) {
        return service.createUser(name, email).thenApply(u -> u);
    }

    @QueryMapping
    @PreAuthorize("hasRole('ADMIN', 'USER')")
    public User getSpecificUser(@Argument String id) {
        return service.getSpecificUser(id);
        // return service.getSpecificUser(id).thenApply(opt -> {
        //     return opt.orElseThrow(() -> new RuntimeException("Unable to get user by that ID"));
        // }).exceptionally(ex -> {
        //     if (ex.getCause() instanceof TimeoutException) {
        //         throw new RuntimeException("User lookup timed out", ex);
        //     }
        //     throw new RuntimeException("Failed to fetch user", ex);
        // });
    }

}
