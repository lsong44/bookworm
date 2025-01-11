package com.li.bookworm.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.time.*;
import java.util.UUID;

@Data
public class Group{
    private UUID id;
    private String name;
    private String announcement=null;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
    private LocalTime startOfTheDay = LocalTime.of(0, 0);
    private int strikeLimit = 2;
    private int maxMembers = 10;

    public Group(){} // needed for POJO defn and JSON deserialization

    public Group(String name) {
        this.name = name;
        this.id = UUID.randomUUID();
    }



}