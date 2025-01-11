package com.li.bookworm.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.time.*;

@Data
public class Role{
    private String name;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt  = LocalDateTime.now();

    public Role(){}
    public Role(String name) {
        this.name = name;
    }
}