package com.li.bookworm.controller;

import com.li.bookworm.constants.ExceptionMessages;
import com.li.bookworm.constants.SuccessMessages;
import com.li.bookworm.model.Group;
import com.li.bookworm.model.Member;
import com.li.bookworm.model.Membership;
import com.li.bookworm.model.Role;
import com.li.bookworm.repository.GroupRepo;
import com.li.bookworm.repository.MemberRepo;
import io.micrometer.common.lang.Nullable;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.li.bookworm.repository.MembershipRepo;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static com.li.bookworm.constants.RoleConstants.*;

@RestController
@RequestMapping("/api")
public class MembershipController {
    @GetMapping("/memberships")
    public ResponseEntity<List<Membership>> listMembership(){
        return new ResponseEntity<>(new ArrayList<>(MembershipRepo.getMembershipAll().values()), HttpStatus.OK);
    }

    @PostMapping("/membership/add")
    // TODO: refactor this method to split add_USER (if not applicable need to add to waitlist), add_WAITLIST, add_ADMIN (need to make sure admin is also user)
    public ResponseEntity<String> addMembership(String groupName, String memberName, String roleName) {
        if(!ROLES.contains(roleName)){
            return new ResponseEntity<>(ExceptionMessages.INVALID_ROLE_EXCEPTION, HttpStatus.BAD_REQUEST);
        }

        Membership existingMembership = MembershipRepo.getMembershipByName(memberName, groupName);
        if( existingMembership != null &&
                (existingMembership.getRole().getName().equals(roleName) ||
                        existingMembership.getRole().getName().equals(WAITLIST))) { // A member can be both admin and user, but cannot have any other role other than waitlist
            return new ResponseEntity<>(ExceptionMessages.MEMBERSHIP_ALREADY_EXISTS_EXCEPTION, HttpStatus.CONFLICT);
        }

        Member member = MemberRepo.getMemberByName(memberName);
        if(member == null) {
            return new ResponseEntity<>(ExceptionMessages.MEMBER_NOT_FOUND_EXCEPTION, HttpStatus.NOT_FOUND);
        }

        Group group = GroupRepo.getGroupByName(groupName);
        if(group == null) {
            return new ResponseEntity<>(ExceptionMessages.GROUP_NOT_FOUND_EXCEPTION, HttpStatus.NOT_FOUND);
        }

        Role role = new Role(roleName);
        Membership newMembership = new Membership(group, member, role);

        if (roleName.equals(USER) || roleName.equals(ADMIN)) {
            if (MembershipRepo.getGroupSize(groupName) < GroupRepo.getGroupByName(groupName).getMaxMembers()) {
                MembershipRepo.addMembership(newMembership);
                if (roleName.equals(ADMIN) && existingMembership == null ) { // ADMIN must also be a USER
                    MembershipRepo.addMembership(new Membership(group, member, new Role(USER)));
                }
                return new ResponseEntity<>(SuccessMessages.ADD_MEMBERSHIP_MESSAGE, HttpStatus.CREATED);
            }
            else {
                return new ResponseEntity<>(ExceptionMessages.GROUP_AT_MAX_CAPACITY, HttpStatus.NOT_FOUND);
            }
        }

        else { // TODO: Add restrictions on the number of waitlists of each group
            MembershipRepo.addMembership(newMembership);
            return new ResponseEntity<>(SuccessMessages.ADD_MEMBERSHIP_MESSAGE, HttpStatus.CREATED);
        }
    }

    @DeleteMapping("/membership/delete")
    public ResponseEntity<String> deleteMembership(@RequestParam String groupName,
                                                   @RequestParam String memberName,
                                                   @Nullable @RequestParam String roleName) {
        if(!ROLES.contains(roleName)){
            return new ResponseEntity<>(ExceptionMessages.INVALID_ROLE_EXCEPTION, HttpStatus.BAD_REQUEST);
        }

        Membership membership = MembershipRepo.getMembershipByName(memberName, groupName);
        if ( membership == null || (roleName != null && !membership.getRole().getName().equals(roleName))) {
            return new ResponseEntity<>(ExceptionMessages.MEMBERSHIP_NOT_FOUND_EXCEPTION, HttpStatus.NOT_FOUND);
        }

        MembershipRepo.deleteMembership(membership);
        return new ResponseEntity<>(SuccessMessages.DELETE_MEMBERSHIP_MESSAGE, HttpStatus.NO_CONTENT);
    }

    @PostMapping("/membership/clean-up")
    public ResponseEntity<String> cleanupMembership(@RequestParam String groupName,
                                                    @RequestParam String memberName,
                                                    @Nullable LocalDate deadlineDate) {
        Group group = GroupRepo.getGroupByName(groupName);
        if ( group == null) {
            return new ResponseEntity<>(ExceptionMessages.GROUP_NOT_FOUND_EXCEPTION, HttpStatus.NOT_FOUND);
        }

        Member member = MemberRepo.getMemberByName(memberName);
        if ( member == null) {
            return new ResponseEntity<>(ExceptionMessages.MEMBER_NOT_FOUND_EXCEPTION, HttpStatus.NOT_FOUND);
        }

        Membership membership = MembershipRepo.getMembershipByName(memberName, groupName);
        if ( membership == null) {
            return new ResponseEntity<>(ExceptionMessages.MEMBERSHIP_NOT_FOUND_EXCEPTION, HttpStatus.NOT_FOUND);
        }

        if (deadlineDate == null) {
            deadlineDate = LocalDate.now();
        }

        if (membership.getRole().getName().equals(USER) && !isActive(member, group, deadlineDate)) {
            MembershipRepo.deleteMembership(membership);
            return new ResponseEntity<>(SuccessMessages.DELETE_MEMBERSHIP_MESSAGE, HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(SuccessMessages.MAINTAIN_MEMBERSHIP_MESSAGE, HttpStatus.NO_CONTENT);

    }

    @PostMapping("/membership/promote/batch")
    public ResponseEntity<String> promoteMembershipByGroup(@RequestParam String groupName) {
        Group group = GroupRepo.getGroupByName(groupName);
        if (group == null) {
            return new ResponseEntity<>(ExceptionMessages.GROUP_NOT_FOUND_EXCEPTION, HttpStatus.NOT_FOUND);
        }

        int currentGroupSize = MembershipRepo.getGroupSize(groupName);
        if (currentGroupSize == group.getMaxMembers()) {
            return new ResponseEntity<>(ExceptionMessages.GROUP_AT_MAX_CAPACITY, HttpStatus.BAD_REQUEST);
        }

        List<Membership> waitlist = MembershipRepo.getWaitlist(groupName);
        if (waitlist.isEmpty()) {
            return new ResponseEntity<>(ExceptionMessages.WAITLIST_EMPTY, HttpStatus.BAD_REQUEST);
        }

        int emptySeats = group.getMaxMembers() - currentGroupSize;
        if (emptySeats > waitlist.size()) {
            for (Membership membership : waitlist) {
                String memberName = membership.getMember().getName();
                promoteMembership(memberName, groupName);
            }
        }
        else{
            waitlist.sort(Comparator.comparing(m -> m.getRole().getCreatedAt()));
            for (int i=0; i<emptySeats; i++) {
                String memberName = waitlist.get(i).getMember().getName();
                promoteMembership(memberName, groupName);
            }
        }

        return new ResponseEntity<>(SuccessMessages.PROMOTE_MEMBERSHIP_MESSAGE, HttpStatus.CREATED);
    }

    @PostMapping("/membership/promote")
    public ResponseEntity<String> promoteMembership(String memberName, String groupName) {
        Membership existingMembership = MembershipRepo.getMembershipByName(memberName, groupName);
        if (existingMembership == null) {
            return new ResponseEntity<>(ExceptionMessages.MEMBERSHIP_NOT_FOUND_EXCEPTION, HttpStatus.NOT_FOUND);
        }

        Role role = existingMembership.getRole();
        if (role.getName().equals(ADMIN)) {
            return new ResponseEntity<>(ExceptionMessages.ROLE_ALREADY_ADMIN, HttpStatus.CONFLICT);
        }
        if (role.getName().equals(USER)) {
            Role newRole = new Role(ADMIN);
            return editMembership(memberName, groupName, newRole);
        }
        // role.getName().equals(WAITLIST)
        if (MembershipRepo.getGroupSize(groupName) < GroupRepo.getGroupByName(groupName).getMaxMembers()) {
            Role newRole = new Role(USER);
            return editMembership(memberName, groupName, newRole);
        }
        else{
            return new ResponseEntity<>(ExceptionMessages.GROUP_AT_MAX_CAPACITY, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/membership/edit")
    public ResponseEntity<String> editMembership(String memberName, String groupName, Role role) {
        Membership existingMembership = MembershipRepo.getMembershipByName(memberName, groupName);
        if (existingMembership == null) {
            return new ResponseEntity<>(ExceptionMessages.MEMBERSHIP_NOT_FOUND_EXCEPTION, HttpStatus.NOT_FOUND);
        }

        existingMembership.setRole(role);
        return new ResponseEntity<>(SuccessMessages.EDIT_MEMBERSHIP_MESSAGE, HttpStatus.CREATED);
    }

    private boolean isActive(Member member, Group group, LocalDate deadlineDate) {
        LocalDateTime memberLastCheckin = member.getLastCheckIn();
        if (memberLastCheckin == null) {
            return false;
        }
        LocalDateTime deadlineCheckin = deadlineDate.atTime(group.getStartOfTheDay());
        long daysBetween = Duration.between(memberLastCheckin, deadlineCheckin).toDays();
        return daysBetween <= group.getStrikeLimit();
    }
}
