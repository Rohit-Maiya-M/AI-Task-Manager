package com.rohit.aitaskmanager.controller;


import com.rohit.aitaskmanager.dto.TaskRequestDTO;
import com.rohit.aitaskmanager.dto.TaskResponseDTO;
import com.rohit.aitaskmanager.models.Priority;
import com.rohit.aitaskmanager.models.Status;
import com.rohit.aitaskmanager.service.TaskPersonalService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/aitaskmanager/personal")
public class TaskPersonalController {
    TaskPersonalService taskPersonalService;

    public TaskPersonalController(TaskPersonalService taskPersonalService){
        this.taskPersonalService = taskPersonalService;
    }

    // Create, Edit, Delete, Filter, Search, Recent, DueDate, Library
    @PostMapping("/create")
    public ResponseEntity<TaskResponseDTO> createTask(
            Authentication auth,
            @Valid @RequestBody TaskRequestDTO dto
            ){
        Long userId = (Long) auth.getPrincipal();
        dto.setUserId(userId);
        TaskResponseDTO createdTask = taskPersonalService.createTask(dto);
        return ResponseEntity.ok(createdTask);
    }

    @PatchMapping("/edit/{taskId}")
    public ResponseEntity<TaskResponseDTO> editTask(
            Authentication auth,
            @Valid @RequestBody TaskRequestDTO dto,
            @PathVariable Long taskId
    ){
        Long userId = (Long) auth.getPrincipal();
        TaskResponseDTO editedTask = taskPersonalService.editTask(userId, taskId, dto);
        return ResponseEntity.ok(editedTask);
    }

    @DeleteMapping("/delete/{taskId}")
    public ResponseEntity<String> deleteTask(
            Authentication auth,
            @PathVariable Long taskId
    ){
        Long userId = (Long) auth.getPrincipal();
        taskPersonalService.deleteTask(userId, taskId);
        return ResponseEntity.ok("Task with id: " + taskId + "is deleted successfully!");
    }

    @GetMapping("/filter")
    public ResponseEntity<Page<TaskResponseDTO>> filterTasks(
            Authentication auth,
            @RequestParam(required = false) Status status,
            @RequestParam(required = false) Priority priority,
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "dueDate") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir
            ){
        Long userId = (Long) auth.getPrincipal();
        Page<TaskResponseDTO> filteredTasks = taskPersonalService.filterTasks(userId, status, priority, category, page, size, sortBy, sortDir);
        return ResponseEntity.ok(filteredTasks);
    }

    @GetMapping("/search")
    public ResponseEntity<Page<TaskResponseDTO>> searchTasks(
            Authentication auth,
            @RequestParam(required = true) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "dueDate") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir
    ){
        Long userId = (Long) auth.getPrincipal();
        Page<TaskResponseDTO> searchedTasks = taskPersonalService.searchTasks(userId, keyword,page, size, sortBy, sortDir);
        return ResponseEntity.ok(searchedTasks);
    }

    @GetMapping("/recents")
    public ResponseEntity<List<TaskResponseDTO>> recentTasks(
            Authentication auth,
            @RequestParam(defaultValue = "7") int days
    ){
        Long userId = (Long) auth.getPrincipal();
        List<TaskResponseDTO> recentTasks = taskPersonalService.recentTasks(userId, days);
        return ResponseEntity.ok(recentTasks);
    }

    @GetMapping("/due-date")
    public ResponseEntity<List<TaskResponseDTO>> getDueSoonTasks(
            Authentication auth,
            @RequestParam(defaultValue = "7") int days
    ){
        Long userId = (Long) auth.getPrincipal();
        List<TaskResponseDTO> dueTasks = taskPersonalService.getDueSoonTasks(userId, days);
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
        Page<TaskResponseDTO> library = taskPersonalService.getLibrary(userId, page, size, sortBy, sortDir);
        return ResponseEntity.ok(library);
    }

}
