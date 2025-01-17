
package com.li.bookworm.controllerTest;

import com.li.bookworm.constants.ExceptionMessages;
import com.li.bookworm.constants.SuccessMessages;
import com.li.bookworm.controller.GroupController;
import com.li.bookworm.model.Group;
import com.li.bookworm.repository.GroupRepo;
import com.li.bookworm.repository.MembershipRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;


public class GroupControllerTest {

	@Mock
	private GroupRepo groupRepo;

	@Mock
	private MembershipRepo membershipRepo;

	@InjectMocks
	private GroupController groupController;

	@BeforeEach
	public void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	public void testListGroups() {
		Map<String, Group> groups = new HashMap<>();
		groups.put("group1", new Group("group1"));
		when(groupRepo.getGroups()).thenReturn(groups);

		ResponseEntity<List<Group>> response = groupController.listGroups();

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(1, response.getBody().size());
		verify(groupRepo, times(1)).getGroups();
	}

	@Test
	public void testDeleteGroup() {
		Group group = new Group("group1");
		when(groupRepo.getGroups()).thenReturn(Map.of("group1", group));

		ResponseEntity<String> response = groupController.deleteGroup("group1");

		assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
		assertEquals(SuccessMessages.DELETE_GROUP_MESSAGE, response.getBody());
		verify(groupRepo, times(1)).deleteGroup(group);
		verify(membershipRepo, times(1)).deleteMembershipByGroup(group);
	}

	@Test
	public void testDeleteGroupNotFound() {
		when(groupRepo.getGroups()).thenReturn(new HashMap<>());

		ResponseEntity<String> response = groupController.deleteGroup("group1");

		assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
		assertEquals(ExceptionMessages.GROUP_NOT_FOUND_EXCEPTION, response.getBody());
	}

	@Test
	public void testAddGroup() {
		when(groupRepo.getGroups()).thenReturn(new HashMap<>());

		ResponseEntity<String> response = groupController.addGroup("group1");

		assertEquals(HttpStatus.CREATED, response.getStatusCode());
		assertEquals(SuccessMessages.ADD_GROUP_MESSAGE, response.getBody());
		verify(groupRepo, times(1)).addGroup(any(Group.class));
	}

	@Test
	public void testAddGroupAlreadyExists() {
		when(groupRepo.getGroups()).thenReturn(Map.of("group1", new Group("group1")));

		ResponseEntity<String> response = groupController.addGroup("group1");

		assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
		assertEquals(ExceptionMessages.GROUP_ALREADY_EXISTS_EXCEPTION, response.getBody());
	}

	@Test
	public void testEditGroup() {
		Group group = new Group("group1");
		when(groupRepo.getGroups()).thenReturn(Map.of("group1", group));
		when(membershipRepo.getGroupSize("group1")).thenReturn(5);

		ResponseEntity<String> response = groupController.editGroup("group1", "New Announcement", LocalTime.NOON, 10, 3);

		assertEquals(HttpStatus.CREATED, response.getStatusCode());
		assertEquals(SuccessMessages.EDIT_GROUP_MESSAGE, response.getBody());
		verify(groupRepo, times(1)).editGroup(group);
	}

	@Test
	public void testEditGroupNotFound() {
		when(groupRepo.getGroups()).thenReturn(new HashMap<>());

		ResponseEntity<String> response = groupController.editGroup("group1", "New Announcement", LocalTime.NOON, 10, 3);

		assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
		assertEquals(ExceptionMessages.GROUP_NOT_FOUND_EXCEPTION, response.getBody());
	}

	@Test
	public void testEditGroupMaxMembersExceeded() {
		Group group = new Group("group1");
		when(groupRepo.getGroups()).thenReturn(Map.of("group1", group));
		when(membershipRepo.getGroupSize("group1")).thenReturn(15);

		ResponseEntity<String> response = groupController.editGroup("group1", "New Announcement", LocalTime.NOON, 10, 3);

		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
		assertEquals(ExceptionMessages.GROUP_AT_MAX_CAPACITY, response.getBody());
	}
}