package com.li.bookworm.stepdefs;

import com.li.bookworm.constants.TestConstants;
import com.li.bookworm.context.HttpContext;

import com.li.bookworm.util.OAuth2TokenUtil;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.BeforeAll;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class StepDefTestGroup {

    CloseableHttpClient httpClient;
    CloseableHttpResponse httpResponse;

    @Autowired
    private TestRestTemplate restTemplate;

//    private static OAuth2TokenUtil oAuth2TokenUtil;

    @Autowired
    private static OAuth2AuthorizedClient authorizedClient;

    private static String bearerAccessToken;

    private static OAuth2TokenUtil oAuth2TokenUtil = new OAuth2TokenUtil();

    @BeforeAll
    public static void setUp() throws GeneralSecurityException, IOException, InterruptedException {
//        oAuth2TokenUtil = new OAuth2TokenUtil();
//        bearerAccessToken = "Bearer " + oAuth2TokenUtil.getAccessToken();
        bearerAccessToken = "Bearer " + TestConstants.ACCESS_TOKEN;
    }

    @Before
    public void init() {
        httpClient = HttpClients.createDefault();
    }

    @After
    public void tearDown() throws IOException {
        httpResponse.close();
        httpClient.close();
    }


    @Given("I hit POST group API with {string} and {string}")
    public void i_hit_register_group_api_with_string_string(String paramName, String paramVal) throws Exception {
        String registerGroupURL = TestConstants.POST_GROUP_ENDPOINT;
        HttpPost request = new HttpPost(registerGroupURL);
        request.setHeader("Authorization", bearerAccessToken);

        List<NameValuePair> urlParameters = new ArrayList<>();
        urlParameters.add(new BasicNameValuePair(paramName, paramVal));

        request.setEntity(new UrlEncodedFormEntity(urlParameters));
        this.httpResponse = httpClient.execute(request);
    }

    @Given("I hit DELETE group API with {string} and {string}")
    public void i_hit_delete_group_api_with_string_string(String paramName, String paramVal) throws IOException {
        String deleteGroupUrl = TestConstants.DELETE_GROUP_ENPOINT;
        HttpDelete request = new HttpDelete(String.format("%s?%s=%s", deleteGroupUrl, paramName, paramVal));
        request.setHeader("Authorization", bearerAccessToken);
        this.httpResponse = httpClient.execute(request);
    }

    @When("I hit GET groups API")
    public void i_hit_get_groups_api() throws Exception {
        String getGroupsURL = TestConstants.GET_GROUPS_ENDPOINT;
        HttpGet request = new HttpGet(getGroupsURL);
        request.setHeader("Authorization", bearerAccessToken);
        httpResponse = httpClient.execute(request);
    }

    @Given("I hit GET groups API with no authorization")
    public void i_hit_get_groups_api_no_authorization() throws IOException {
        String getGroupsURL = TestConstants.GET_GROUPS_ENDPOINT;
        HttpGet request = new HttpGet(getGroupsURL);
        httpResponse = httpClient.execute(request);
    }

    @Then("I get {int} response")
    public void i_get_int_response(int statusCode) throws Exception {
        assertNotNull(httpResponse);
        assertEquals(statusCode, httpResponse.getStatusLine().getStatusCode());
    }

    @Then("My response contains {string}:{string}")
    public void my_response_contains_string_string(String paramName, String paramVal) throws IOException {
        String responseBody = EntityUtils.toString(httpResponse.getEntity());
        verifyRepsonseContains(responseBody, String.format("\"%s\":\"%s\"", paramName, paramVal));
    }

    private void verifyRepsonseContains(String actual, String expected) {
        assertTrue(actual.contains(expected));
    }

}
