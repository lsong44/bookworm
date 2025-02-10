package com.li.bookworm.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Data
public class Member{

    private UUID id;
    private String name;
    private String email;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime lastCheckIn=null;

    public Member(){
    }

    public Member(String name, String email){
        this.name = name;
        this.email = email;
        this.id = UUID.randomUUID();
    }

    public Member(String name, String email, LocalDateTime lastCheckIn){
        this.name = name;
        this.email = email;
        this.lastCheckIn = lastCheckIn;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Member other = (Member) obj;
        return this.getName().equals(other.getName()) && this.getEmail().equals(other.getEmail());
    }

    @Override
    public int hashCode() {
        return id.hashCode() + 31 * name.hashCode() + 11 * email.hashCode();
    }

}