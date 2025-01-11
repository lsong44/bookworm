package com.li.bookworm.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class BookLog {
    private UUID id;
    private Member member;
    private String bookTitle;
    private String comment=null;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime timestamp;

    public BookLog(){
    }

    public BookLog(Member member, String bookTitle){
        this.id = UUID.randomUUID();
        this.member = member;
        this.bookTitle = bookTitle;
        this.timestamp = LocalDateTime.now();
    }

    public BookLog(Member member, String bookTitle, String comment){
        this(member, bookTitle);
        this.comment = comment;
    }

    public BookLog(Member member, String bookTitle, String comment, LocalDateTime timestamp) {
        this(member, bookTitle, comment);
        this.timestamp = timestamp;
    }
}
