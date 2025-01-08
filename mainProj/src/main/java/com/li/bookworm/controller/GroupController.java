package com.li.bookworm.controller;

import com.li.bookworm.constants.ExceptionMessages;
import com.li.bookworm.constants.SuccessMessages;
import com.li.bookworm.model.Group;
import com.li.bookworm.repository.GroupRepo;
import io.micrometer.common.lang.Nullable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api")
public class GroupController {

	@GetMapping("/groups")
	public ResponseEntity<List<Group>> listGroups() {
		ArrayList<Group> groupList = new ArrayList<>(GroupRepo.getGroups().values());
		return new ResponseEntity<>(groupList, HttpStatus.OK);
	}

	@PostMapping("/group/delete")
	public ResponseEntity<String> deleteGroup(@RequestParam String name) {
		Group existingGroup = GroupRepo.getGroups().get(name);
		if ( existingGroup == null) {
			return new ResponseEntity<>(ExceptionMessages.GROUP_NOT_FOUND_EXCEPTION, HttpStatus.NOT_FOUND);
		}
		GroupRepo.deleteGroup(existingGroup);
		return new ResponseEntity<>(SuccessMessages.DELETE_GROUP_MESSAGE, HttpStatus.NO_CONTENT);
	}

	@PostMapping("/group/register")
	public ResponseEntity<String> addGroup(@RequestParam String name) {

		if (GroupRepo.getGroups().get(name) != null) {
			return new ResponseEntity<>(ExceptionMessages.GROUP_ALREADY_EXISTS_EXCEPTION, HttpStatus.CONFLICT);
		}
		Group newGroup = new Group(name);
		GroupRepo.addGroup(newGroup);
		return new ResponseEntity<>(SuccessMessages.ADD_GROUP_MESSAGE, HttpStatus.CREATED);
	}

	@PutMapping("/group/edit")
	public ResponseEntity<String> editGroup(String name, @Nullable String announcement, @Nullable LocalTime startOfTheDay,
											@Nullable Integer maxMembers, @Nullable Integer strikeLimit) {

		Group existingGroup = GroupRepo.getGroups().get(name);
		if (existingGroup == null) {
			return new ResponseEntity<>(ExceptionMessages.GROUP_NOT_FOUND_EXCEPTION, HttpStatus.NOT_FOUND);
		}

		if (announcement != null) existingGroup.setAnnouncement(announcement);
		if (startOfTheDay != null) existingGroup.setStartOfTheDay(startOfTheDay);
		if (maxMembers != null) existingGroup.setMaxMembers(maxMembers);
		if (strikeLimit != null) existingGroup.setStrikeLimit(strikeLimit);

		return new ResponseEntity<>(SuccessMessages.EDIT_GROUP_MESSAGE, HttpStatus.CREATED);
	}

}
