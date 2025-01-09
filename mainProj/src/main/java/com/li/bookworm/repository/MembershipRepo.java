package com.li.bookworm.repository;

import com.li.bookworm.constants.RoleConstants;
import com.li.bookworm.model.Group;
import com.li.bookworm.model.Member;
import com.li.bookworm.model.Membership;
import com.li.bookworm.model.Role;
import com.li.bookworm.util.Tuple;

import java.util.*;

public class MembershipRepo {
    public static Map<Tuple<String, String>,List<Membership>> membershipAll = new HashMap<>();

    public static Map<Tuple<String, String>,List<Membership>> getMembershipAll() {
        return membershipAll;
    }

    public static List<Membership> getMembershipByName(String memberName, String groupName) {
        Tuple<String, String> key = new Tuple<>(memberName, groupName);
        return membershipAll.get(key);
    }

    public static void addMembership(Membership membership){
        if (membershipAll.containsKey(membership.getKey())) {
            membershipAll.get(membership.getKey()).add(membership);
        }
        else {membershipAll.put(membership.getKey(),
                new ArrayList<>(Collections.singletonList(membership)));
        }
    }

    public static void deleteMembership(Membership membership) {
        List<Membership> memberships = membershipAll.get(membership.getKey());
        memberships.remove(membership);
        if (memberships.isEmpty()) {
            membershipAll.remove(membership.getKey());
        }
    }

    public static void deleteMembershipByMember(Member member) {
        deleteMembershipBatch(member.getName(), 1);
    }

    public static void deleteMembershipByGroup(Group group) {
        deleteMembershipBatch(group.getName(), 2);
    }

    public static int getGroupSize(String groupName) {
        return getGroupUsers(groupName).size();
    }

    public static List<Membership> getGroupUsers(String groupName) {
        List<Membership> groupUsers = new ArrayList<>();
        for (Map.Entry<Tuple<String, String>, List<Membership>> entry : membershipAll.entrySet()) {
            String entryGroupName = entry.getKey().second;
            for(Membership entryMembership : entry.getValue()) {
                Role entryMemberRole = entryMembership.getRole();
                if (entryGroupName.equals(groupName) && entryMemberRole.getName().equals(RoleConstants.USER) ) {
                    groupUsers.add(entryMembership);
                }
            }
        }
        return groupUsers;
    }

    public static List<Membership> getWaitlist(String groupName) {
        List<Membership> waitlist = new ArrayList<>();
        for (Map.Entry<Tuple<String, String>, List<Membership>> entry : membershipAll.entrySet()) {
            String entryGroupName = entry.getKey().second;
            for(Membership entryMembership : entry.getValue()) {
                Role entryMemberRole = entryMembership.getRole();
                if (entryGroupName.equals(groupName) && entryMemberRole.getName().equals(RoleConstants.WAITLIST) ) {
                    waitlist.add(entryMembership);
                }
            }
        }
        return waitlist;
    }

    private static void deleteMembershipBatch(String keyName, int whichKey) {
        List<Tuple<String, String>> keysToRemove = new ArrayList<>();
        for (Tuple<String, String> key : membershipAll.keySet()) {
            String currentKeyName = null;
            if (whichKey == 1) currentKeyName = key.first;
            else if (whichKey == 2) currentKeyName = key.second;
            if (currentKeyName != null && currentKeyName.equals(keyName)) {
                keysToRemove.add(key);
            }
        }

        for (Tuple<String, String> key : keysToRemove) {
            membershipAll.remove(key);
        }
    }
}
