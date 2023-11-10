package com.example.userservice.User;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public enum Role {
    ADMIN(
            Set.of(
                    Permission.ADMIN_READ,
                    Permission.ADMIN_UPDATE,
                    Permission.ADMIN_DELETE,
                    Permission.ADMIN_CREATE,
                    Permission.DELIVER_PERSON_CREATE,
                    Permission.DELIVER_PERSON_UPDATE,
                    Permission.DELIVER_PERSON_READ,
                    Permission.DELIVER_PERSON_DELETE,
                    Permission.CUSTOMER_CREATE,
                    Permission.CUSTOMER_READ,
                    Permission.CUSTOMER_UPDATE,
                    Permission.CUSTOMER_DELETE
            )
    ),

    CUSTOMER(
            Set.of(
                    Permission.CUSTOMER_CREATE,
                    Permission.CUSTOMER_READ,
                    Permission.CUSTOMER_UPDATE,
                    Permission.CUSTOMER_DELETE
            )
    ),

    DELIVERY_PERSON(
            Set.of(
                    Permission.DELIVER_PERSON_CREATE,
                    Permission.DELIVER_PERSON_DELETE,
                    Permission.DELIVER_PERSON_UPDATE,
                    Permission.DELIVER_PERSON_READ
            )
    );

    @Getter
    private final Set<Permission> permissions;

    public List<SimpleGrantedAuthority> grantedAuthorities() {
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();

        authorities.addAll(getPermissions()
                .stream()
                .map(permission -> new SimpleGrantedAuthority(permission.name()))
                .collect(Collectors.toList()));

        authorities.add(new SimpleGrantedAuthority("ROLE_" + this.name())); // Fixed the missing underscore

        return Collections.unmodifiableList(authorities);
    }


}
