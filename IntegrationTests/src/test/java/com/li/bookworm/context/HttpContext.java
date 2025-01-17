package com.li.bookworm.context;

import lombok.Data;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

public class HttpContext {

    public static final CloseableHttpClient httpClient = HttpClients.createDefault();

}
