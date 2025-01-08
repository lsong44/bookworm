package com.li.bookworm.constants;

import java.util.HashSet;
import java.util.Set;

public class RoleConstants {
    public static final String ADMIN = "ADMIN";
    public static final String USER = "USER";
    public static final String WAITLIST = "WAITLIST";
    public static final Set<String> ROLES = new HashSet<>(Set.of(ADMIN, USER, WAITLIST));
}
