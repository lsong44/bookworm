package com.li.bookworm.config;


import com.azure.cosmos.*;
import com.azure.cosmos.models.CosmosContainerProperties;
import com.azure.cosmos.models.ThroughputProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.li.bookworm.constants.DatabaseConstants.*;

@Configuration
public class DatabaseConfig {

    @Value("${cosmosdb.endpoint}")
    private String DB_HOST;

    @Value("${cosmosdb.key}")
    private String DB_KEY;

    @Value("${cosmosdb.database}")
    private String DB_NAME;

    @Value("${cosmosdb.throughput}")
    private int THROUGHPUT;
    

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
        
        ThroughputProperties throughputProperties = ThroughputProperties.createManualThroughput(THROUGHPUT);
        CosmosContainerProperties containerProperties =
                new CosmosContainerProperties(containerName, partitionKey);

        cosmosDatabase.createContainerIfNotExists(containerProperties, throughputProperties).block();
        return cosmosDatabase.getContainer(containerName);
    }
}
