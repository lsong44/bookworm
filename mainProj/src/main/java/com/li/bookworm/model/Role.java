package com.li.bookworm.model;

import lombok.Data;
import java.time.*;

@Data
public class Role{
    private String name;
    private LocalDateTime createdAt  = LocalDateTime.now();

    public Role(String name) {
        this.name = name;
    }
}