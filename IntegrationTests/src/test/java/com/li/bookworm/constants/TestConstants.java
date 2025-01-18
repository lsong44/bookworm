package com.li.bookworm.constants;

import org.springframework.beans.factory.annotation.Value;

public class TestConstants {

    public static final String HOST = System.getenv("TEST_HOST");
    public static final String GET_GROUPS_ENDPOINT = HOST + "/api/groups";
    public static final String POST_GROUP_ENDPOINT = HOST + "/api/group/register";
    public static final String DELETE_GROUP_ENPOINT = HOST + "/api/group/delete";

}
