package com.li.bookworm.repository;

import com.azure.cosmos.CosmosAsyncContainer;
import com.azure.cosmos.models.CosmosItemRequestOptions;
import com.azure.cosmos.models.CosmosQueryRequestOptions;
import com.azure.cosmos.models.PartitionKey;
import com.azure.cosmos.util.CosmosPagedFlux;
import com.li.bookworm.model.BookLog;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class BookLogRepo {
    private final CosmosAsyncContainer container;
    private List<BookLog> bookLogs;

    @Autowired
    public BookLogRepo(CosmosAsyncContainer cosmosBookLogContainer){
        this.container = cosmosBookLogContainer;
        this.bookLogs = loadAllBookLogs();
    }

    public List<BookLog> getBookLogs() {
        return loadAllBookLogs();
    }

    public void addBookLog(BookLog log) {
        bookLogs.add(log);
        PartitionKey partitionKey = new PartitionKey(log.getMember().getName());
        CosmosItemRequestOptions cosmosItemRequestOptions = new CosmosItemRequestOptions();
        container.createItem(log, partitionKey, cosmosItemRequestOptions).block();
    }

    private List<BookLog> loadAllBookLogs() {
        List<BookLog> allBookLogs = new ArrayList<>();

        CosmosPagedFlux<BookLog> pagedFlux = container.queryItems(
                "SELECT * FROM c", new CosmosQueryRequestOptions(), BookLog.class);

        pagedFlux.byPage().toIterable().forEach(feedResponse -> {
            allBookLogs.addAll(feedResponse.getResults());
        });

        return allBookLogs;
    }

}
