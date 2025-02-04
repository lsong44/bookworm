package com.li.bookworm.constants;

import org.springframework.beans.factory.annotation.Value;

public class TestConstants {

    public static final String HOST = System.getenv("TEST_HOST");
    public static final String GET_GROUPS_ENDPOINT = HOST + "/api/groups";
    public static final String POST_GROUP_ENDPOINT = HOST + "/api/group/register";
    public static final String DELETE_GROUP_ENPOINT = HOST + "/api/group/delete";

    public static final String OAUTH_URL = "https://accounts.google.com/o/oauth2/auth";
    public static final String CALLBACK_PATH = "/login/oauth2/code/google";
    public static final String REDIRECT_URL = HOST + CALLBACK_PATH;
    public static final String SCOPE = System.getenv("OAUTH_SCOPE");
    public static final String CLIENT_ID = System.getenv("OAUTH_CLIENT_ID");
    public static final String CLIENT_SECRET = System.getenv("OAUTH_CLIENT_SECRET");
    public static final String ACCESS_TOKEN = System.getenv("OAUTH_ACCESS_TOKEN");

    public static final int HTTP_PORT = 80;

}
