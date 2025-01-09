package com.li.bookworm.constants;

import java.util.HashSet;
import java.util.Set;

public class RoleConstants {
    public static final String ADMIN = "ADMIN";
    public static final String USER = "USER";
    public static final String WAITLIST = "WAITLIST";
    public static final Set<String> ROLES = new HashSet<>(Set.of(ADMIN, USER, WAITLIST));
    public static final int MAX_ROLES_PER_MEMBER_GROUP = 2; // one can be ADMIN, USER or WAITLIST only, or ADMIN & USER together, or ADMIN & WAITLIST together
}
