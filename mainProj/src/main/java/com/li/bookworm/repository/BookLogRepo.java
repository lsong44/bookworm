package com.li.bookworm.repository;

import com.li.bookworm.model.BookLog;

import java.util.ArrayList;
import java.util.List;

public class BookLogRepo {
    private static List<BookLog> bookLogs = new ArrayList<BookLog>();

    public static List<BookLog> getBookLogs(){
        return bookLogs;
    }

    public static void addBookLog(BookLog log) {
        bookLogs.add(log);
    }
}
