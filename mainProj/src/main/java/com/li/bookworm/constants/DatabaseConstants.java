package com.li.bookworm.constants;

public class DatabaseConstants {
    public static final String DB_PREFIX = System.getenv().get("DB_NAME") + "-";
    public static final String MEMBER_CONTAINER = "members";
    public static final String GROUP_CONTAINER = "groups";
    public static final String MEMBERSHIP_CONTAINER = "memberships";
    public static final String BOOKLOG_CONTAINER = "booklogs";
}
