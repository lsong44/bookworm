package com.li.bookworm.controller;

import com.azure.cosmos.CosmosAsyncContainer;
import com.li.bookworm.constants.ExceptionMessages;
import com.li.bookworm.constants.SuccessMessages;
import com.li.bookworm.model.Group;
import com.li.bookworm.repository.GroupRepo;
import com.li.bookworm.repository.MembershipRepo;
import io.micrometer.common.lang.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class GroupController {

	@Autowired
	private GroupRepo groupRepo;

	@Autowired
	private MembershipRepo membershipRepo;

	@GetMapping("/groups")
	public ResponseEntity<List<Group>> listGroups() {
		ArrayList<Group> groupList = new ArrayList<>(groupRepo.getGroups().values());
		return new ResponseEntity<>(groupList, HttpStatus.OK);
	}

	@DeleteMapping("/group/delete")
	public ResponseEntity<String> deleteGroup(@RequestParam String name) {
		Group existingGroup = groupRepo.getGroups().get(name);
		if ( existingGroup == null) {
			return new ResponseEntity<>(ExceptionMessages.GROUP_NOT_FOUND_EXCEPTION, HttpStatus.NOT_FOUND);
		}
		groupRepo.deleteGroup(existingGroup);
		membershipRepo.deleteMembershipByGroup(existingGroup); // if a group is deleted, remove all membership for that group
		return new ResponseEntity<>(SuccessMessages.DELETE_GROUP_MESSAGE, HttpStatus.NO_CONTENT);
	}

	@PostMapping("/group/register")
	public ResponseEntity<String> addGroup(@RequestParam String name) {

		if (groupRepo.getGroups().get(name) != null) {
			return new ResponseEntity<>(ExceptionMessages.GROUP_ALREADY_EXISTS_EXCEPTION, HttpStatus.CONFLICT);
		}
		Group newGroup = new Group(name);
		groupRepo.addGroup(newGroup);
		return new ResponseEntity<>(SuccessMessages.ADD_GROUP_MESSAGE, HttpStatus.CREATED);
	}

	@PutMapping("/group/edit")
	public ResponseEntity<String> editGroup(@RequestParam String name,
											@RequestParam @Nullable String announcement,
											@RequestParam @Nullable LocalTime startOfTheDay,
											@RequestParam @Nullable Integer maxMembers,
											@RequestParam @Nullable Integer strikeLimit) {

		Group existingGroup = groupRepo.getGroups().get(name);
		if (existingGroup == null) {
			return new ResponseEntity<>(ExceptionMessages.GROUP_NOT_FOUND_EXCEPTION, HttpStatus.NOT_FOUND);
		}

		if (announcement != null) existingGroup.setAnnouncement(announcement);
		if (startOfTheDay != null) existingGroup.setStartOfTheDay(startOfTheDay);
		if (strikeLimit != null) existingGroup.setStrikeLimit(strikeLimit);

		if (maxMembers != null) {
			if (membershipRepo.getGroupSize(existingGroup.getName()) > maxMembers) {
				return new ResponseEntity<>(ExceptionMessages.GROUP_AT_MAX_CAPACITY, HttpStatus.BAD_REQUEST);
			}
			existingGroup.setMaxMembers(maxMembers);
		}
		groupRepo.editGroup(existingGroup);
		return new ResponseEntity<>(SuccessMessages.EDIT_GROUP_MESSAGE, HttpStatus.CREATED);
	}

}
