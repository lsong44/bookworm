package com.li.bookworm.config;


import com.azure.cosmos.*;
import com.azure.cosmos.models.CosmosContainerProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.li.bookworm.constants.DatabaseConstants.*;

@Configuration
public class DatabaseConfig {

    private final String DB_HOST;
    private final String DB_KEY;
    private final String DB_NAME;

    @Autowired
    public DatabaseConfig() {
        this.DB_HOST = System.getenv("ACCOUNT_HOST");
        this.DB_KEY = System.getenv("ACCOUNT_KEY");
        this.DB_NAME = System.getenv("DB_NAME");
    }
    

    @Bean
    public CosmosAsyncClient cosmosClient() {
        return new CosmosClientBuilder()
                .endpoint(DB_HOST)
                .key(DB_KEY)
                .consistencyLevel(ConsistencyLevel.SESSION)
                .buildAsyncClient();
    }

    @Bean
    public CosmosAsyncDatabase cosmosDatabase(CosmosAsyncClient cosmosClient) {
        cosmosClient.createDatabaseIfNotExists(DB_NAME).block();
        return cosmosClient.getDatabase(DB_NAME);
    }

    @Bean 
    public CosmosAsyncContainer cosmosMemberContainer(CosmosAsyncDatabase cosmosDatabase) {
        String containerName = DB_PREFIX + MEMBER_CONTAINER;
        String partitionKey = "/name";
        return createCosmosContainer(cosmosDatabase, containerName, partitionKey);
    }

    @Bean
    public CosmosAsyncContainer cosmosGroupContainer(CosmosAsyncDatabase cosmosDatabase) {
        String containerName = DB_PREFIX + GROUP_CONTAINER;
        String partitionKey = "/name";
        return createCosmosContainer(cosmosDatabase, containerName, partitionKey);
    }

    @Bean
    public CosmosAsyncContainer cosmosMembershipContainer(CosmosAsyncDatabase cosmosDatabase) {
        String containerName = DB_PREFIX + MEMBERSHIP_CONTAINER;
        String partitionKey = "/group/name";
        return createCosmosContainer(cosmosDatabase, containerName, partitionKey);
    }

    @Bean
    public CosmosAsyncContainer cosmosBookLogContainer(CosmosAsyncDatabase cosmosDatabase) {
        String containerName = DB_PREFIX + BOOKLOG_CONTAINER;
        String partitionKey = "/member/name";
        return createCosmosContainer(cosmosDatabase, containerName, partitionKey);
    }

    private CosmosAsyncContainer createCosmosContainer(CosmosAsyncDatabase cosmosDatabase,
                                                       String containerName,
                                                       String partitionKey) {
        CosmosContainerProperties containerProperties =
                new CosmosContainerProperties(containerName, partitionKey);

        cosmosDatabase.createContainerIfNotExists(containerProperties).block();
        return cosmosDatabase.getContainer(containerName);
    }
}
