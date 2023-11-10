package com.example.userservice.User;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Permission {

    ADMIN_READ("admin:read"),
    ADMIN_UPDATE("admin:update"),
    ADMIN_CREATE("admin:create"),
    ADMIN_DELETE("admin:delete"),
    DELIVER_PERSON_READ("delivery:read"),
    DELIVER_PERSON_UPDATE("delivery:update"),
    DELIVER_PERSON_CREATE("delivery:create"),
    DELIVER_PERSON_DELETE("delivery:delete"),
    CUSTOMER_READ("customer:read"),
    CUSTOMER_UPDATE("customer:update"),
    CUSTOMER_CREATE("customer:create"),
    CUSTOMER_DELETE("customer:delete");


    @Getter
    private final String permission;
}
