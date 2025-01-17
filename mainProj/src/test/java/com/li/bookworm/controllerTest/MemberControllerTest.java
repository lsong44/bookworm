package com.li.bookworm.controllerTest;

import com.li.bookworm.controller.MemberController;
import com.li.bookworm.controller.MembershipController;
import com.li.bookworm.model.Member;
import com.li.bookworm.repository.MemberRepo;
import com.li.bookworm.repository.MembershipRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class MemberControllerTest {
    @Mock
    private MemberRepo memberRepo;

    @Mock
    private MembershipRepo membershipRepo;

    @InjectMocks
    private MemberController memberController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testListMembers() {
        Map<String, Member> members = new HashMap<>();
        Member member1 = new Member("member1", "member1@members.org");
        members.put(member1.getName(), member1);
        when(memberRepo.getMembers()).thenReturn(members);

        ResponseEntity<List<Member>> response = memberController.listMembers();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        verify(memberRepo, times(1)).getMembers();

    }
}
