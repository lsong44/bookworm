package com.li.bookworm.model;

import lombok.Data;
import java.time.*;
import java.time.format.DateTimeFormatter;

@Data
public class Member{

    private String name;
    private String email;
    private LocalDateTime lastCheckIn=null;

    public Member(String name, String email){
        this.name = name;
        this.email = email;
    }

    public Member(String name, String email, LocalDateTime lastCheckIn){
        this.name = name;
        this.email = email;
        this.lastCheckIn = lastCheckIn;
    }

}