package com.li.bookworm.repository;

import com.li.bookworm.constants.RoleConstants;
import com.li.bookworm.model.Membership;
import com.li.bookworm.model.Role;
import com.li.bookworm.util.Tuple;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MembershipRepo {
    public static Map<Tuple<String, String>,Membership> membershipAll = new HashMap<>();

    public static Map<Tuple<String, String>,Membership> getMembershipAll() {
        return membershipAll;
    }

    public static Membership getMembershipByName(String memberName, String groupName) {
        Tuple<String, String> key = new Tuple<>(memberName, groupName);
        return membershipAll.get(key);
    }

    public static void addMembership(Membership membership){
        membershipAll.put(membership.getKey(), membership);
    }

    public static void deleteMembership(Membership membership) {

        membershipAll.remove(membership.getKey());
    }

    public static int getGroupSize(String groupName) {
        int size = 0;
        for (Map.Entry<Tuple<String, String>, Membership> entry : membershipAll.entrySet()) {
            String entryGroupName = entry.getKey().second;
            Membership entryMembership = entry.getValue();
            Role entryMemberRole = entryMembership.getRole();
            if (entryGroupName.equals(groupName) && entryMemberRole.getName().equals(RoleConstants.USER) ) {
                size++;
            }
        }
        return size;
    }

    public static List<Membership> getWaitlist(String groupName) {
        List<Membership> waitlist = new ArrayList<>();
        for (Map.Entry<Tuple<String, String>, Membership> entry : membershipAll.entrySet()) {
            String entryGroupName = entry.getKey().second;
            Membership entryMembership = entry.getValue();
            Role entryMemberRole = entryMembership.getRole();
            if (entryGroupName.equals(groupName) && entryMemberRole.getName().equals(RoleConstants.WAITLIST) ) {
                waitlist.add(entryMembership);
            }
        }
        return waitlist;
    }
}
