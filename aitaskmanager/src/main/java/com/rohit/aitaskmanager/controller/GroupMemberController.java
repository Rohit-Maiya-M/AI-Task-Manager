package com.rohit.aitaskmanager.controller;


import com.rohit.aitaskmanager.dto.TaskResponseDTO;
import com.rohit.aitaskmanager.models.Priority;
import com.rohit.aitaskmanager.models.Status;
import com.rohit.aitaskmanager.service.GroupMemberService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/aitaskmanager/group/member")
public class GroupMemberController {

    private final GroupMemberService groupMemberService;

    public GroupMemberController(GroupMemberService groupMemberService){
        this.groupMemberService = groupMemberService;
    }

    @GetMapping("/view/assignedTasks/{groupId}")
    public ResponseEntity<Page<TaskResponseDTO>> viewTasks(
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
        return ResponseEntity.ok(groupMemberService.viewTasksAssignedToMember(userId, groupId, status, priority, category, page, size, sortBy, sortDir));
    }

    @PatchMapping("/complete/{groupId}/{taskId}")
    public ResponseEntity<TaskResponseDTO> completeTask(
            Authentication auth,
            @PathVariable Long groupId,
            @PathVariable Long taskId
    ){
        Long userId = (Long) auth.getPrincipal();
        return ResponseEntity.ok(groupMemberService.completeTask(userId, groupId, taskId));
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
        Page<TaskResponseDTO> filteredTasks = groupMemberService.filterTasks(userId, groupId, status, priority, category, page, size, sortBy, sortDir);
        return ResponseEntity.ok(filteredTasks);
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
        Page<TaskResponseDTO> searchTasks = groupMemberService.searchTasks(userId, groupId, keyword, page, size, sortBy, sortDir);
        return ResponseEntity.ok(searchTasks);
    }

    @GetMapping("/recents/{days}")
    public ResponseEntity<List<TaskResponseDTO>> recentTasks(
            Authentication auth,
            @PathVariable int days
    ){
        Long userId = (Long) auth.getPrincipal();
        List<TaskResponseDTO> recentTasks = groupMemberService.recentTasks(userId, days);
        return ResponseEntity.ok(recentTasks);
    }

    @GetMapping("/due-date/{days}")
    public ResponseEntity<List<TaskResponseDTO>> getDueSoonTasks(
            Authentication auth,
            @PathVariable int days
    ){
        Long userId = (Long) auth.getPrincipal();
        List<TaskResponseDTO> dueTasks = groupMemberService.getDueSoonTasks(userId, days);
        return ResponseEntity.ok(dueTasks);
    }

    @GetMapping("/library")
    public ResponseEntity<Page<TaskResponseDTO>> getLibrary(
            Authentication auth,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "dueDate") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir
    ){
        Long userId = (Long) auth.getPrincipal();
        Page<TaskResponseDTO> libraryTasks = groupMemberService.getLibrary(userId, page, size, sortBy, sortDir);
        return ResponseEntity.ok(libraryTasks);
    }

}
