package com.li.bookworm.model;

import lombok.Data;
import java.time.*;

@Data
public class Group{
    private String name;
    private String announcement=null;
    private LocalTime startOfTheDay = LocalTime.of(0, 0);
    private int strikeLimit = 2;
    private int maxMembers = 10;

    public Group(String name) {
        this.name = name;
    }

}