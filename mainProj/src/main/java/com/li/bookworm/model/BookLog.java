package com.li.bookworm.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BookLog {
    private LocalDateTime timestamp = LocalDateTime.now();
    private Member member;
    private String bookTitle;
    private String comment=null;

    public BookLog(Member member, String bookTitle){
        this.member = member;
        this.bookTitle = bookTitle;
    }

    public BookLog(Member member, String bookTitle, String comment){
        this.member = member;
        this.bookTitle = bookTitle;
        this.comment = comment;
    }

    public BookLog(Member member, String bookTitle, String comment, LocalDateTime timestamp) {
        this.member = member;
        this.bookTitle = bookTitle;
        this.comment = comment;
        this.timestamp = timestamp;
    }
}
