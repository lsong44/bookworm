package com.li.bookworm.repository;

import com.azure.cosmos.CosmosAsyncContainer;
import com.azure.cosmos.models.CosmosItemRequestOptions;
import com.azure.cosmos.models.CosmosQueryRequestOptions;
import com.azure.cosmos.models.PartitionKey;
import com.azure.cosmos.util.CosmosPagedFlux;
import com.li.bookworm.model.Group;
import com.li.bookworm.model.Member;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class MemberRepo {
    private final CosmosAsyncContainer container;
    private Map<String, Member> members;

    @Autowired
    public MemberRepo(CosmosAsyncContainer cosmosMemberContainer) {
        this.container = cosmosMemberContainer;
        members = loadAllMembers();
    }

    public Map<String, Member> getMembers() {
        return loadAllMembers();
    }

    public Member getMemberByName(String memberName) {
        return getMembers().get(memberName);
    }

    public void addMember(Member member) {
        members.put(member.getName(), member);

        PartitionKey partitionKey = new PartitionKey(member.getName());
        container.createItem(member, partitionKey, new CosmosItemRequestOptions()).block();
    }

    public void deleteMember(Member member) {

        members.remove(member.getName());

        PartitionKey partitionKey = new PartitionKey(member.getName());
        container.deleteItem(member.getId().toString(), partitionKey).block();
    }

    private Map<String, Member> loadAllMembers() {
        Map<String, Member> allMembers = new HashMap<>();

        CosmosPagedFlux<Member> pagedFlux = container.queryItems(
                "SELECT * FROM c", new CosmosQueryRequestOptions(), Member.class);

        pagedFlux.byPage().toIterable().forEach(feedResponse -> {
            feedResponse.getResults().forEach(member -> allMembers.put(member.getName(), member));
        });

        return allMembers;
    }

}
