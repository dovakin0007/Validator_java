package com.dovakin0007.notes_service.security.CustomClaims;

import com.dovakin0007.notes_service.enums.Role;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CustomClaims {
    @Getter
    @Setter
    private String user_id;

    @Getter
    @Setter
    private String type;

    @Getter
    @Setter
    private List<Role> role;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static CustomClaims fromClaims(Claims claims) {
        return objectMapper.convertValue(claims, CustomClaims.class);
    }
}
