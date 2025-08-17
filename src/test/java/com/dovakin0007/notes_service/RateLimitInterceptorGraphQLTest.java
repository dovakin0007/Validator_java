package com.dovakin0007.notes_service;



import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class RateLimitInterceptorGraphQLTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldRateLimitAfterFiveRequests() throws Exception {
        String query = """
        { "query": "{ listUsers { id name email } }" }
        """;

        for (int i = 0; i < 5; i++) {
            mockMvc.perform(post("/api/project-managment")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(query))
                    .andExpect(status().isOk());
        }

        mockMvc.perform(post("/api/project-managment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(query))
                .andExpect(status().isTooManyRequests());
    }
}
