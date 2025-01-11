package com.li.bookworm.controller;

import com.li.bookworm.constants.ExceptionMessages;
import com.li.bookworm.constants.SuccessMessages;
import com.li.bookworm.model.Member;
import com.li.bookworm.repository.MemberRepo;
import com.li.bookworm.repository.MembershipRepo;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
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

	@PostMapping("/member/register")
	public ResponseEntity<String> addMember(@RequestParam String name, @RequestParam String email) {
		if (memberRepo.getMemberByName(name) != null) {
			return new ResponseEntity<>(ExceptionMessages.MEMBER_ALREADY_EXISTS_EXCEPTOIN, HttpStatus.CONFLICT);
		}
		Member newMember = new Member(name, email);
		memberRepo.addMember(newMember);
		return new ResponseEntity<>(SuccessMessages.ADD_MEMBER_MESSAGE, HttpStatus.CREATED);
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
	public ResponseEntity<String> editMember(@RequestParam String name, @RequestParam String email) {
		Member member = memberRepo.getMemberByName(name);
		if(member == null){
			return new ResponseEntity<>(ExceptionMessages.MEMBER_NOT_FOUND_EXCEPTION, HttpStatus.NOT_FOUND);
		}
		member.setEmail(email);
		return new ResponseEntity<>(SuccessMessages.EDIT_MEMBERSHIP_MESSAGE, HttpStatus.CREATED);
	}

}
