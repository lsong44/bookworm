package com.li.bookworm.repository;

import com.azure.cosmos.CosmosAsyncContainer;
import com.azure.cosmos.models.CosmosItemRequestOptions;
import com.azure.cosmos.models.CosmosQueryRequestOptions;
import com.azure.cosmos.models.PartitionKey;
import com.azure.cosmos.util.CosmosPagedFlux;
import com.li.bookworm.constants.RoleConstants;
import com.li.bookworm.model.Group;
import com.li.bookworm.model.Member;
import com.li.bookworm.model.Membership;
import com.li.bookworm.model.Role;
import com.li.bookworm.util.Tuple;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class MembershipRepo {

    private final CosmosAsyncContainer container;

    @Autowired
    public MembershipRepo(CosmosAsyncContainer cosmosMembershipContainer) {
        this.container = cosmosMembershipContainer;
    }

    public Map<Tuple<String, String>,List<Membership>> getMembershipAll() {
        return loadAllMemberships();
    }

    public List<Membership> getMembershipByName(String memberName, String groupName) {
        Tuple<String, String> key = new Tuple<>(memberName, groupName);
        return getMembershipAll().get(key);
    }

    public void addMembership(Membership membership){
        Map<Tuple<String, String>,List<Membership>> membershipAll = getMembershipAll();
        if (membershipAll.containsKey(membership.getKey())) {
            membershipAll.get(membership.getKey()).add(membership);
        }
        else {membershipAll.put(membership.getKey(),
                new ArrayList<>(Collections.singletonList(membership)));
        }

        PartitionKey partitionKey = new PartitionKey(membership.getGroup().getName());
        container.createItem(membership, partitionKey, new CosmosItemRequestOptions()).block();
    }

    public void deleteMembership(Membership membership) {
        List<Membership> memberships = getMembershipAll().get(membership.getKey());
        memberships.remove(membership);

        PartitionKey partitionKey = new PartitionKey(membership.getGroup().getName());
        container.deleteItem(membership.getId().toString(), partitionKey).block();

    }

    public Membership editMembershipRole(Membership membership, Role newRole) {
        Role oldRole = membership.getRole();
        Tuple<String, String> key = membership.getKey();
        List<Membership> memberships = getMembershipAll().get(key);
        for(Membership m : memberships) {
            if (m.getRole().getName().equals(oldRole.getName())) {
                PartitionKey partitionKey = new PartitionKey(m.getGroup().getName());
                CosmosItemRequestOptions options = new CosmosItemRequestOptions();
                container.deleteItem(m.getId().toString(), partitionKey, options).block();
                m.setRole(newRole);
                container.createItem(m, partitionKey, options).block();
                return m;
            }
        }
        return null;
    }

    public List<Membership> getMembershipByMember(Member member) {
        String memberName = member.getName();
        List<Membership> memberships = new ArrayList<>();
        Map<Tuple<String, String>,List<Membership>> membershipAll = getMembershipAll();
        for (Map.Entry<Tuple<String, String>, List<Membership>> entry : membershipAll.entrySet()) {
            String entryMemberName = entry.getKey().first;
            for(Membership entryMembership : entry.getValue()) {
                if (entryMemberName.equals(memberName) ) {
                    memberships.add(entryMembership);
                }
            }
        }
        return memberships;
    }

    public void deleteMembershipByMember(Member member) {
        deleteMembershipBatch(member.getName(), 1);
    }

    public void deleteMembershipByGroup(Group group) {
        deleteMembershipBatch(group.getName(), 2);
    }

    public int getGroupSize(String groupName) {
        return getGroupUsers(groupName).size();
    }

    public List<Membership> getGroupUsers(String groupName) {
        return getGroupMembershipByRoles(groupName, RoleConstants.USER);
    }

    public List<Membership> getWaitlist(String groupName) {
        return getGroupMembershipByRoles(groupName, RoleConstants.WAITLIST);
    }

    public List<Membership> getGroupAdmins(String groupName) {
        return getGroupMembershipByRoles(groupName, RoleConstants.ADMIN);
    }

    private List<Membership> getGroupMembershipByRoles(String groupName, String roleName) {
        List<Membership> memberships = new ArrayList<>();
        Map<Tuple<String, String>,List<Membership>> membershipAll = getMembershipAll();
        for (Map.Entry<Tuple<String, String>, List<Membership>> entry : membershipAll.entrySet()) {
            String entryGroupName = entry.getKey().second;
            for(Membership entryMembership : entry.getValue()) {
                Role entryMemberRole = entryMembership.getRole();
                if (entryGroupName.equals(groupName) && entryMemberRole.getName().equals(roleName) ) {
                    memberships.add(entryMembership);
                }
            }
        }
        return memberships;
    }
    private Map<Tuple<String, String>,List<Membership>> loadAllMemberships() {
        Map<Tuple<String, String>,List<Membership>> allMemberships = new HashMap<>();

        CosmosPagedFlux<Membership> pagedFlux = container.queryItems(
                "SELECT * FROM c", new CosmosQueryRequestOptions(), Membership.class);
        pagedFlux.byPage().toIterable().forEach(membershipFeedResponse -> {
            for (Membership membership : membershipFeedResponse.getResults()) {
                Tuple<String, String> key = membership.getKey();
                if (!allMemberships.keySet().isEmpty() && allMemberships.keySet().contains(key)) {
                    allMemberships.get(key).add(membership);
                }
                else {
                    allMemberships.put(key, new ArrayList<Membership>(Collections.singletonList(membership)));
                }
            }
        });
        return allMemberships;
    }

    private void deleteMembershipBatch(String keyName, int whichKey) {
        List<Tuple<String, String>> keysToRemove = new ArrayList<>();
        Map<Tuple<String, String>,List<Membership>> membershipAll = getMembershipAll();
        for (Tuple<String, String> key : membershipAll.keySet()) {
            String currentKeyName = null;
            if (whichKey == 1) currentKeyName = key.first;
            else if (whichKey == 2) currentKeyName = key.second;
            if (currentKeyName != null && currentKeyName.equals(keyName)) {
                keysToRemove.add(key);
            }
        }

        for (Tuple<String, String> key : keysToRemove) {
            List<Membership> membershipsToRemove = membershipAll.get(key);
            membershipsToRemove.forEach(this::deleteMembership);

            membershipAll.remove(key);
        }
    }
}
