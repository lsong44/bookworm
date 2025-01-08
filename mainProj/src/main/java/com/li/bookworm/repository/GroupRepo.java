package com.li.bookworm.repository;

import com.li.bookworm.model.Group;

import java.util.*;

public class GroupRepo {
    public static Map<String, Group> groups = new HashMap<String, Group>();

    public static Map<String, Group> getGroups() {
        return groups;
    }

    public static Group getGroupByName(String name) {
        return groups.get(name);
    }

    public static void addGroup(Group group) {
        groups.put(group.getName(), group);
    }

    public static void deleteGroup(Group group) {
        groups.remove(group.getName());
    }
}
