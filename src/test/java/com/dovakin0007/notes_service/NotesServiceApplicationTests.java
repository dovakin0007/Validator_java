// package com.dovakin0007.notes_service;

// import org.junit.jupiter.api.Test;
// import org.springframework.boot.test.context.SpringBootTest;
// @SpringBootTest
// class NotesServiceApplicationTests {

// 	@Test
// 	void contextLoads() {
// 	}

// }

package com.dovakin0007.notes_service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.dovakin0007.notes_service.service.GrpcUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.dovakin0007.notes_service.controller.UsersController;
import com.dovakin0007.notes_service.models.User;
// import com.dovakin0007.notes_service.service.UserService;

@ExtendWith(MockitoExtension.class)
class UsersControllerTest {

    // @Mock
    // private UserService userService;

    @Mock
    private GrpcUserService grpcUserService;

    private UsersController usersController;

    @BeforeEach
    void setUp() {
        usersController = new UsersController(grpcUserService);
    }

    @Test
    void testListUsers_ShouldReturnUsersList() throws Exception {
        // Given
        User user1 = new User("1", "John Doe", "john@example.com", "avatar1.jpg", "2024-01-01", "2024-01-01", "Bio 1");
        User user2 = new User("2", "Jane Smith", "jane@example.com", "avatar2.jpg", "2024-01-02", "2024-01-02", "Bio 2");
        List<User> expectedUsers = List.of(user1, user2);
        
        when(grpcUserService.listAllUsers());

        // When
        List<User> actualUsers = usersController.listUsers();

        // Then
        assertNotNull(actualUsers);
        assertEquals(2, actualUsers.size());
        assertEquals("John Doe", actualUsers.get(0).name());
        verify(grpcUserService).listAllUsers();
    }

    @Test
    void testCreateUser_ShouldReturnCreatedUser() throws Exception {
        // Given
        String name = "John Doe";
        String email = "john@example.com";
        User expectedUser = new User("1", name, email, "avatar.jpg", "2024-01-01", "2024-01-01", "Test bio");
        
        when(grpcUserService.createUser(name, email))
            .thenReturn(CompletableFuture.completedFuture(expectedUser));

        // When
        User actualUser = usersController.createUser(name, email).toCompletableFuture().get();

        // Then
        assertNotNull(actualUser);
        assertEquals("1", actualUser.id());
        assertEquals(name, actualUser.name());
        assertEquals(email, actualUser.email());
        verify(grpcUserService).createUser(name, email);
    }
}