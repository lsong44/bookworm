package com.li.bookworm.repository;

import com.azure.cosmos.CosmosAsyncContainer;
import com.azure.cosmos.models.CosmosItemRequestOptions;
import com.azure.cosmos.models.CosmosQueryRequestOptions;
import com.azure.cosmos.models.PartitionKey;
import com.azure.cosmos.util.CosmosPagedFlux;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.li.bookworm.constants.CacheKeyConstants;
import com.li.bookworm.model.BookLog;
import com.li.bookworm.model.Group;
import com.li.bookworm.service.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Repository
public class GroupRepo {
    private final CosmosAsyncContainer container;
    private final RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private RedisService redisService;

    @Value("${spring.redis.use}")
    private boolean USE_CACHE;

    @Value("${spring.redis.timeout}")
    private int CACHE_TIMEOUT;

    @Autowired
    public GroupRepo(CosmosAsyncContainer cosmosGroupContainer,
                     RedisTemplate<String, Object> redisTemplate) {
        this.container = cosmosGroupContainer;
        this.redisTemplate = redisTemplate;
    }

    public Map<String, Group> getGroups() {
        return loadAllGroups();
    }

    public Group getGroupByName(String name) {
        return getGroups().get(name);
    }

    public void addGroup(Group group) {
        PartitionKey partitionKey = new PartitionKey(group.getName());
        CosmosItemRequestOptions cosmosItemRequestOptions = new CosmosItemRequestOptions();
        container.createItem(group, partitionKey, cosmosItemRequestOptions).block();

        if (USE_CACHE) redisService.updateGroupCache(group, "ADD");

    }

    public void deleteGroup(Group group) {

        PartitionKey partitionKey = new PartitionKey(group.getName());
        CosmosItemRequestOptions cosmosItemRequestOptions = new CosmosItemRequestOptions();
        container.deleteItem(group.getId().toString(), partitionKey, cosmosItemRequestOptions).block();

        if (USE_CACHE) redisService.updateGroupCache(group, "DELETE");
    }

    public void editGroup(Group group) {

        PartitionKey partitionKey = new PartitionKey(group.getName());
        CosmosItemRequestOptions cosmosItemRequestOptions = new CosmosItemRequestOptions();
        container.deleteItem(group.getId().toString(), partitionKey, cosmosItemRequestOptions).block();
        container.createItem(group, partitionKey, cosmosItemRequestOptions).block();

        if (USE_CACHE) redisService.updateGroupCache(group, "EDIT");

    }

    private Map<String, Group> loadAllGroups() {
        Map<String, Group> allGroups;

        if (USE_CACHE) {
            allGroups = redisService.getGroupsFromCache();
        } else {
            allGroups = new HashMap<>();
        }

        if(allGroups.isEmpty()) {

           CosmosPagedFlux<Group> pagedFlux = container.queryItems(
                   "SELECT * FROM c", new CosmosQueryRequestOptions(), Group.class);

           pagedFlux.byPage().toIterable().forEach(feedResponse -> {
               feedResponse.getResults().forEach(group -> allGroups.put(group.getName(), group));
           });

           redisTemplate.opsForValue().set(CacheKeyConstants.GROUP_ALL_CACHE_KEY, CacheKeyConstants.GROUP_ALL_CACHE_KEY);
       }

        return allGroups;
    }

}
