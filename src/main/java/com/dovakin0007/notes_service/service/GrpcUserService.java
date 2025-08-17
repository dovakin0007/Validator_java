package com.dovakin0007.notes_service.service;

import com.dovakin0007.notes_service.exceptions.UserCreationFailedException;
import com.dovakin0007.notes_service.exceptions.UserNotAvailableException;
import com.dovakin0007.notes_service.models.User;
import com.dovakin0007.userservice.*;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.protobuf.Empty;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.grpc.StatusRuntimeException;
import lombok.NonNull;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
public class GrpcUserService {

    @GrpcClient("user-service")
    private UserServiceGrpc.UserServiceFutureStub userNonBlockingStub;

    @GrpcClient("user-service")
    private UserServiceGrpc.UserServiceBlockingStub userBlockingStub;

    @CircuitBreaker(name = "createNewUser", fallbackMethod = "createNewUserFallback")
    public CompletableFuture<User> createUser(@NonNull String name, @NonNull String email) {
        try {
            var request = CreateUserRequest.newBuilder()
                    .setName(name)
                    .setEmail(email)
                    .build();

            var response = userNonBlockingStub.createUser(request);
            CompletableFuture<User> cf = new CompletableFuture<User>();
            Futures.addCallback(response, new FutureCallback<>() {
                @Override
                public void onSuccess(CreateUserResponse result) {
                    cf.complete(mapUser(result.getUser()));
                }

                @Override
                public void onFailure(Throwable t) {
                    cf.completeExceptionally(t);
                }

            }, MoreExecutors.directExecutor());
            return cf;
        } catch (StatusRuntimeException e) {
            throw new RuntimeException("Failed to create user: " + e.getStatus(), e);
        }
    }

    public CompletableFuture<User> createNewUserFallback(@NonNull String name, @NonNull String email, Throwable t) {
        return CompletableFuture.failedFuture(
              new UserCreationFailedException("Failed to create user", t)
        );
    }



    @CircuitBreaker(name = "listUsers", fallbackMethod = "listUsersFallback")
    public List<User> listAllUsers() {
        try {
            ListUsersResponse response = userBlockingStub.listAllUser(Empty.getDefaultInstance());
            return response.getUsersList().stream()
                    .map(this::mapUser)
                    .collect(Collectors.toList());
        } catch (StatusRuntimeException e) {
            throw new RuntimeException("Failed to list users: " + e.getStatus(), e);
        }
    }

    public List<User> listUsersFallback(Throwable t) {
        return Collections.emptyList();
    }

    @CircuitBreaker(name = "getUser", fallbackMethod="getUserFallback")
    public User getSpecificUser(String id) {
        try {
            var request = GetUserRequest.newBuilder()
                    .setId(id)
                    .build();

            var response = userBlockingStub.getSpecificUser(request);
            return mapUser(response);
        } catch (StatusRuntimeException e) {
            throw new RuntimeException("Failed to get user: " + e.getStatus(), e);
        }
    }
    public User getUserFallback(String id, Throwable t) {
        throw new UserNotAvailableException("User service unavailable, could not fetch user with id: " + id);
    }



    private User mapUser(com.dovakin0007.userservice.User protoUser) {
        return new User(
                protoUser.getId(),
                protoUser.getName(),
                protoUser.getEmail(),
                protoUser.hasAvatarUrl() ? protoUser.getAvatarUrl() : null,
                protoUser.getCreatedAt(),
                protoUser.getUpdatedAt(),
                protoUser.hasBio() ? protoUser.getBio() : null);
    }
}
