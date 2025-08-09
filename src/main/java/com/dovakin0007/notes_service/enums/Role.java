package com.dovakin0007.notes_service.enums;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.core.authority.SimpleGrantedAuthority;

import static com.dovakin0007.notes_service.enums.Privilege.DELETE_PRIVILEGE;
import static com.dovakin0007.notes_service.enums.Privilege.READ_PRIVILEGE;
import static com.dovakin0007.notes_service.enums.Privilege.UPDATE_PRIVILEGE;
import static com.dovakin0007.notes_service.enums.Privilege.WRITE_PRIVILEGE;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Role {
        ADMIN(
                        Set.of(READ_PRIVILEGE, WRITE_PRIVILEGE, UPDATE_PRIVILEGE, DELETE_PRIVILEGE)),
        USER(
                        Set.of(READ_PRIVILEGE, WRITE_PRIVILEGE));

        private final Set<Privilege> privileges;

        public List<SimpleGrantedAuthority> getAuthorities() {
                var authorities = getPrivileges()
                                .stream()
                                .map(privelege -> new SimpleGrantedAuthority(privelege.name()))
                                .collect(Collectors.toList());
                authorities.add(new SimpleGrantedAuthority("ROLE_" + this.name()));
                return authorities;
        }
}
