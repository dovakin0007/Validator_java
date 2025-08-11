package com.dovakin0007.notes_service.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Service;

import com.dovakin0007.notes_service.models.User;

import lombok.NonNull;

@Service
public class UserService implements DefaultServiceImpl {
    private final Map<String, User> users = new HashMap<>(10, 0.9f);

    public Map<String, User> getUsers() {
        return users;
    }

    @Override
    public String createUuid() {
        return UUID.randomUUID().toString();
    }

    // TODO(Gowtham): These will get converted to microservice calls
    public CompletableFuture<User> createUser(@NonNull String name, @NonNull String email) {
        return CompletableFuture.supplyAsync(() -> {
            Date currentDate = new Date();
            String uuid = createUuid();
            User user = new User(uuid, name, email, null, currentDate.toString(), currentDate.toString(), null);
            users.put(uuid, user);
            return user;

        });

    }

    // TODO: These will get converted to microservice calls
    public CompletableFuture<Optional<List<User>>> listAllUser() {
        return CompletableFuture.supplyAsync(() -> {
           List<User> list = new ArrayList<>(users.values());
           return list.isEmpty() ? Optional.empty() : Optional.of(list);
        });
    }

    public CompletableFuture<Optional<User>> getSpecificUser(@NonNull String key) {
            return CompletableFuture.supplyAsync(() -> Optional.ofNullable(users.get(key))).orTimeout(1, TimeUnit.SECONDS);
        }

}
