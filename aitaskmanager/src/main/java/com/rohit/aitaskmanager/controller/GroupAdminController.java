package com.rohit.aitaskmanager.controller;


import com.rohit.aitaskmanager.dto.*;
import com.rohit.aitaskmanager.models.Priority;
import com.rohit.aitaskmanager.models.Status;
import com.rohit.aitaskmanager.service.GroupAdminService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/aitaskmanager/group/admin")
public class GroupAdminController {

    private final GroupAdminService groupAdminService;

    public GroupAdminController(GroupAdminService groupAdminService){
        this.groupAdminService = groupAdminService;
    }

    @PostMapping("/login")
    public ResponseEntity<String> loginToGroup(
            Authentication auth,
            @Valid @RequestBody GroupLoginRequestDTO dto
    ) {
        Long userId = (Long) auth.getPrincipal();
        groupAdminService.verifyGroupAdminAccess(userId, dto.getGroupId(), dto.getPassword());
        return ResponseEntity.ok("Authentication Successful!");
    }

    @PostMapping("/create/{groupId}")
    public ResponseEntity<TaskResponseDTO> createTaskForGroup(
            Authentication auth,
            @PathVariable Long groupId,
            @Valid @RequestBody TaskRequestDTO dto
    ){
        Long userId = (Long) auth.getPrincipal();
        return ResponseEntity.ok(groupAdminService.createTaskForGroup(userId, groupId, dto));
    }

    @PostMapping("/create/{groupId}/{assignedUserId}")
    public ResponseEntity<TaskResponseDTO> createTaskForMember(
            Authentication auth,
            @PathVariable Long groupId,
            @PathVariable Long assignedUserId,
            @Valid @RequestBody TaskRequestDTO dto
    ){
        Long userId = (Long) auth.getPrincipal();
        return ResponseEntity.ok(groupAdminService.createTaskForMember(userId, assignedUserId, groupId, dto));
    }

    @PutMapping("/edit/{groupId}/{taskId}")
    public ResponseEntity<TaskResponseDTO> editTaskForGroup(
            Authentication auth,
            @PathVariable Long groupId,
            @PathVariable Long taskId,
            @Valid @RequestBody TaskRequestDTO dto
    ){
        Long userId = (Long) auth.getPrincipal();
        return ResponseEntity.ok(groupAdminService.editTaskForGroup(userId, groupId, taskId, dto));
    }

    @PutMapping("/edit/{groupId}/{taskId}/{assignedUserId}")
    public ResponseEntity<TaskResponseDTO> editTaskForMember(
            Authentication auth,
            @PathVariable Long assignedUserId,
            @PathVariable Long groupId,
            @PathVariable Long taskId,
            @Valid @RequestBody TaskRequestDTO dto
    ){
        Long userId = (Long) auth.getPrincipal();
        return ResponseEntity.ok(groupAdminService.editTaskForMember(userId, assignedUserId, groupId, taskId, dto));
    }

    @PatchMapping("/complete/{groupId}/{taskId}")
    public ResponseEntity<TaskResponseDTO> completeTask(
            Authentication auth,
            @PathVariable Long groupId,
            @PathVariable Long taskId
    ){
        Long userId = (Long) auth.getPrincipal();
        return ResponseEntity.ok(groupAdminService.completeTask(userId, groupId, taskId));
    }

    @PatchMapping("/assign/{assignedUserId}/{groupId}/{taskId}")
    public ResponseEntity<TaskResponseDTO> assignTaskForMember(
            Authentication auth,
            @PathVariable Long assignedUserId,
            @PathVariable Long groupId,
            @PathVariable Long taskId
    ){
        Long userId = (Long) auth.getPrincipal();
        return ResponseEntity.ok(groupAdminService.assignTaskForMember(userId, assignedUserId, groupId, taskId));
    }

    @PostMapping("/createGroup")
    public ResponseEntity<GroupMemberResponseDTO> createGroup(
            Authentication auth,
            @Valid @RequestBody GroupCreationRequestDTO dto
            ){
        Long userId = (Long) auth.getPrincipal();
        return ResponseEntity.ok(groupAdminService.createGroup(userId, dto));
    }


    @PostMapping("/createMember")
    public ResponseEntity<GroupMemberResponseDTO> createMember(
            Authentication auth,
            @Valid @RequestBody GroupMemberRequestDTO dto
    ){
        Long userId = (Long) auth.getPrincipal();
        return ResponseEntity.ok(groupAdminService.createMember(userId, dto));
    }

    @GetMapping("/members/{groupId}")
    public ResponseEntity<List<GroupMemberResponseDTO>> viewMembers(
            Authentication auth,
            @PathVariable Long groupId
    ) {
        Long userId = (Long) auth.getPrincipal();
        return ResponseEntity.ok(groupAdminService.viewMembers(userId, groupId));
    }

    @DeleteMapping("/deleteMember")
    public ResponseEntity<String> deleteMember(
            Authentication auth,
            @Valid @RequestBody GroupMemberRequestDTO dto
    ){
        Long userId = (Long) auth.getPrincipal();
        groupAdminService.deleteMember(userId, dto);
        return ResponseEntity.ok("Deleted Member Successfully!");
    }

    @GetMapping("/filter/{groupId}")
    public ResponseEntity<Page<TaskResponseDTO>> filterTasks(
            Authentication auth,
            @PathVariable Long groupId,
            @RequestParam(required = false) Status status,
            @RequestParam(required = false) Priority priority,
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "dueDate") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir
    ){
        Long userId = (Long) auth.getPrincipal();
        Page<TaskResponseDTO> filteredTasks = groupAdminService.filterTasks(userId, groupId, status, priority, category, page, size, sortBy, sortDir);
        return ResponseEntity.ok(filteredTasks);
    }

    @GetMapping("/check")
    public ResponseEntity<String> check(
            Authentication auth
    ){
        return ResponseEntity.ok("Es geht!");
    }

    @GetMapping("/search/{groupId}")
    public ResponseEntity<Page<TaskResponseDTO>> searchTasks(
            Authentication auth,
            @PathVariable Long groupId,
            @RequestParam(required = true) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "dueDate") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir
    ){
        Long userId = (Long) auth.getPrincipal();
        Page<TaskResponseDTO> searchedTasks = groupAdminService.searchTasks(userId, groupId, keyword, page, size, sortBy, sortDir);
        return ResponseEntity.ok(searchedTasks);
    }


}
