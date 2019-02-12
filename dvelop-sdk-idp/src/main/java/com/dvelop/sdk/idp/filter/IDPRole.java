package com.dvelop.sdk.idp.filter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface IDPRole {

    static enum IDPRoles {
        ANONYMOUS,
        USER_EXTERNAL,
        USER_INTERNAL,
        ADMIN_TENANT,
    }

    IDPRoles value();
}
