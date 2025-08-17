package com.dovakin0007.notes_service.service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.dovakin0007.notes_service.models.User;
import com.dovakin0007.userservice.CreateUserRequest;
import com.dovakin0007.userservice.CreateUserResponse;
import com.dovakin0007.userservice.GetUserRequest;
import com.dovakin0007.userservice.ListUsersResponse;
import com.dovakin0007.userservice.UserServiceGrpc;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.protobuf.Empty;

import io.grpc.StatusRuntimeException;
import lombok.NonNull;
import net.devh.boot.grpc.client.inject.GrpcClient;

@Service
public class GrpcUserService {

    @GrpcClient("user-service") // This must match your application.properties grpc.client.<name>...
    private UserServiceGrpc.UserServiceFutureStub userNonBlockingStub;

    @GrpcClient("user-service")
    private UserServiceGrpc.UserServiceBlockingStub userBlockingStub;

    public CompletableFuture<User> createUser(@NonNull String name, @NonNull String email)  {
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

    public void listAllUser() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
