package com.li.bookworm.controller;

import com.li.bookworm.constants.ExceptionMessages;
import com.li.bookworm.constants.SuccessMessages;
import com.li.bookworm.model.BookLog;
import com.li.bookworm.model.Member;
import com.li.bookworm.repository.BookLogRepo;
import com.li.bookworm.repository.MemberRepo;
import io.micrometer.common.lang.Nullable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api")
public class LogBookController {

    @GetMapping("/logs")
    public ResponseEntity<List<BookLog>> listBookLogs(){
        return new ResponseEntity<>(BookLogRepo.getBookLogs(), HttpStatus.OK);
    }

    @PostMapping("/log/add")
    public ResponseEntity<String> addLog(@RequestParam String name,
                                         @RequestParam String title,
                                         @RequestParam @Nullable String comment,
                                         @RequestParam @Nullable LocalDateTime timestamp){
        Member member = MemberRepo.getMemberByName(name);
        if ( member == null){
            return new ResponseEntity<>(ExceptionMessages.MEMBER_NOT_FOUND_EXCEPTION, HttpStatus.NOT_FOUND);
        }
        BookLog newLog = new BookLog(member, title);
        if (comment != null) {
            newLog.setComment(comment);
        }
        if (timestamp != null) {
            newLog.setTimestamp(timestamp);
        }
        BookLogRepo.addBookLog(newLog);
        member.setLastCheckIn(newLog.getTimestamp());
        return new ResponseEntity<>(SuccessMessages.ADD_BOOK_LOG_MESSAGE, HttpStatus.CREATED);
    }
}
