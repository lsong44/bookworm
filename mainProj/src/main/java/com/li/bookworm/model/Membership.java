package com.li.bookworm.model;

import com.li.bookworm.util.Tuple;
import lombok.Data;

import java.util.UUID;

@Data
public class Membership{
    private UUID id;
    private Group group;
    private Member member;
    private Role role;

    public Membership(){}

    public Membership(Group group, Member member, Role role) {
        this.id = UUID.randomUUID();
        this.group = group;
        this.member = member;
        this.role = role;
    }

    public Tuple<String, String> getKey() {
        return new Tuple<>(member.getName(), group.getName());
    }
}