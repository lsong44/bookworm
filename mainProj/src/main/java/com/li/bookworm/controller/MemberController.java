package com.li.bookworm.controller;

import com.li.bookworm.constants.ExceptionMessages;
import com.li.bookworm.constants.SuccessMessages;
import com.li.bookworm.model.Member;
import com.li.bookworm.repository.MemberRepo;
import com.li.bookworm.repository.MembershipRepo;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.autoconfigure.observation.ObservationProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api")
public class MemberController {
	@Autowired
	private MemberRepo memberRepo;

	@Autowired
	private MembershipRepo membershipRepo;

	@GetMapping("/members")
	public ResponseEntity<List<Member>> listMembers(){

		return new ResponseEntity<>(new ArrayList<Member>(memberRepo.getMembers().values()), HttpStatus.OK);
	}

	@GetMapping("/member")
	public ResponseEntity<Member> getMember(@RequestParam String name) {
		Member currentMember = memberRepo.getMemberByName(name);
		if (currentMember == null) {
			return new ResponseEntity<>(currentMember, HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<>(currentMember, HttpStatus.OK);
	}

	@PostMapping("/member/register")
	public ResponseEntity<Member> addMember(@RequestParam String name, @RequestParam String email) {
		if (memberRepo.getMemberByName(name) != null) {
			return new ResponseEntity<>(new Member(name, email), HttpStatus.CONFLICT);
		}
		Member newMember = new Member(name, email);
		memberRepo.addMember(newMember);
		return new ResponseEntity<Member>(newMember, HttpStatus.CREATED);
	}

	@DeleteMapping("/member/delete")
	public ResponseEntity<String> deleteMember(@RequestParam String name) {
		Member member = memberRepo.getMemberByName(name);
		if(member == null){
			return new ResponseEntity<>(ExceptionMessages.MEMBER_NOT_FOUND_EXCEPTION, HttpStatus.NOT_FOUND);
		}
		memberRepo.deleteMember(member);
		membershipRepo.deleteMembershipByMember(member); // if a member is deleted, remove all membership for that member
		return new ResponseEntity<>(SuccessMessages.DELETE_MEMBER_MESSAGE, HttpStatus.NO_CONTENT);
	}

	@PutMapping("/member/edit")
	public ResponseEntity<Member> editMember(@RequestParam String name, @RequestParam String email) {
		Member member = memberRepo.getMemberByName(name);
		if(member == null){
			return new ResponseEntity<>(member, HttpStatus.NOT_FOUND);
		}
		member.setEmail(email);
		return new ResponseEntity<Member>(member, HttpStatus.CREATED);
	}

}
