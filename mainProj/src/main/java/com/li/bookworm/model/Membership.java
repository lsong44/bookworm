package com.li.bookworm.model;

import com.li.bookworm.util.Tuple;
import lombok.Data;

@Data
public class Membership{
    private Group group;
    private Member member;
    private Role role;

    public Membership(Group group, Member member, Role role) {
        this.group = group;
        this.member = member;
        this.role = role;
    }

    public Tuple<String, String> getKey() {
        return new Tuple<>(member.getName(), group.getName());
    }
}