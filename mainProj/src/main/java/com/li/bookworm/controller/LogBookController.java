package com.li.bookworm.controller;

import com.li.bookworm.constants.ExceptionMessages;
import com.li.bookworm.constants.SuccessMessages;
import com.li.bookworm.model.BookLog;
import com.li.bookworm.model.Member;
import com.li.bookworm.repository.BookLogRepo;
import com.li.bookworm.repository.MemberRepo;
import io.micrometer.common.lang.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api")
public class LogBookController {

    @Autowired
    private BookLogRepo bookLogRepo;

    @Autowired
    private MemberRepo memberRepo;

    @GetMapping("/logs")
    public ResponseEntity<List<BookLog>> listBookLogs(){
        return new ResponseEntity<>(bookLogRepo.getBookLogs(), HttpStatus.OK);
    }

    @GetMapping("/log")
    public ResponseEntity<List<BookLog>> getBookLogForMember(@RequestParam String memberName) {
        Member member = memberRepo.getMemberByName(memberName);
        if ( member == null){
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(bookLogRepo.getBookLogByMember(member), HttpStatus.OK);
    }

    @PostMapping("/log/add")
    public ResponseEntity<BookLog> addLog(@RequestParam String memberName,
                                         @RequestParam String title,
                                         @RequestParam @Nullable String comment,
                                         @RequestParam @Nullable LocalDateTime timestamp){
        Member member = memberRepo.getMemberByName(memberName);
        if ( member == null){
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
        BookLog newLog = new BookLog(member, title);
        if (comment != null) {
            newLog.setComment(comment);
        }
        if (timestamp != null) {
            newLog.setTimestamp(timestamp);
        }
        bookLogRepo.addBookLog(newLog);
        if(member.getLastCheckIn() == null || newLog.getTimestamp().isAfter(member.getLastCheckIn())) {
            member.setLastCheckIn(newLog.getTimestamp());
        }

        return new ResponseEntity<>(newLog, HttpStatus.CREATED);
    }
}
