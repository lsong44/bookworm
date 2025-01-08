package com.li.bookworm.repository;

import com.li.bookworm.model.Member;

import java.util.*;

public class MemberRepo {
    private static Map<String, Member> members = new HashMap<>();

    public static Map<String, Member> getMembers() {
        return members;
    }

    public static Member getMemberByName(String memberName) {
        return members.get(memberName);
    }

    public static void addMember(Member member) {
        members.put(member.getName(), member);
    }

    public static void deleteMember(Member member) {
        members.remove(member.getName());
    }

}
