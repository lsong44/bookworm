package com.li.bookworm.repository;

import com.azure.cosmos.CosmosAsyncContainer;
import com.azure.cosmos.models.CosmosItemRequestOptions;
import com.azure.cosmos.models.CosmosQueryRequestOptions;
import com.azure.cosmos.models.PartitionKey;
import com.azure.cosmos.util.CosmosPagedFlux;
import com.li.bookworm.model.BookLog;
import com.li.bookworm.model.Member;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class BookLogRepo {
    private final CosmosAsyncContainer container;
    private List<BookLog> logs;

    @Autowired
    public BookLogRepo(CosmosAsyncContainer cosmosBookLogContainer){

        this.container = cosmosBookLogContainer;
        this.logs = loadAllBookLogs();
    }

    public List<BookLog> getBookLogs() {

        return this.logs;
    }

    public List<BookLog> getBookLogByMember(Member member) {
        this.logs = loadAllBookLogs();
        List<BookLog> logsForGivenMember = new ArrayList<>();
        for (BookLog log : this.logs) {
            if (log.getMember().equals(member)) {
                logsForGivenMember.add(log);
            }
        }
        return logsForGivenMember;
    }

    public void addBookLog(BookLog log) {
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
