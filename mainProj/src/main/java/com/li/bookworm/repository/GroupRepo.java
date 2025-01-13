package com.li.bookworm.repository;

import com.azure.cosmos.CosmosAsyncContainer;
import com.azure.cosmos.models.CosmosItemRequestOptions;
import com.azure.cosmos.models.CosmosQueryRequestOptions;
import com.azure.cosmos.models.PartitionKey;
import com.azure.cosmos.util.CosmosPagedFlux;
import com.li.bookworm.model.BookLog;
import com.li.bookworm.model.Group;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class GroupRepo {
    private final CosmosAsyncContainer container;
    public Map<String, Group> groups;

    @Autowired
    public GroupRepo(CosmosAsyncContainer cosmosGroupContainer) {
        this.container = cosmosGroupContainer;
        this.groups = loadAllGroups();
    }
    public Map<String, Group> getGroups() {
        return loadAllGroups();
    }

    public Group getGroupByName(String name) {
        return getGroups().get(name);
    }

    public void addGroup(Group group) {
        getGroups().put(group.getName(), group);

        PartitionKey partitionKey = new PartitionKey(group.getName());
        CosmosItemRequestOptions cosmosItemRequestOptions = new CosmosItemRequestOptions();
        container.createItem(group, partitionKey, cosmosItemRequestOptions).block();
    }

    public void deleteGroup(Group group) {
        getGroups().remove(group.getName());

        PartitionKey partitionKey = new PartitionKey(group.getName());
        CosmosItemRequestOptions cosmosItemRequestOptions = new CosmosItemRequestOptions();
        container.deleteItem(group.getId().toString(), partitionKey, cosmosItemRequestOptions).block();
    }

    public void editGroup(Group group) {

        deleteGroup(group);
        addGroup(group);
    }

    private Map<String, Group> loadAllGroups() {
        Map<String, Group> allGroups = new HashMap<>();

        CosmosPagedFlux<Group> pagedFlux = container.queryItems(
                "SELECT * FROM c", new CosmosQueryRequestOptions(), Group.class);

        pagedFlux.byPage().toIterable().forEach(feedResponse -> {
            feedResponse.getResults().forEach(group -> allGroups.put(group.getName(), group));
        });

        return allGroups;
    }
}
