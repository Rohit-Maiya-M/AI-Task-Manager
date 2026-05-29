package com.rohit.aitaskmanager.service;

import com.rohit.aitaskmanager.dto.TaskResponseDTO;
import com.rohit.aitaskmanager.exception.TaskNotFoundException;
import com.rohit.aitaskmanager.exception.UnauthorizedException;
import com.rohit.aitaskmanager.models.*;
import com.rohit.aitaskmanager.repository.TaskGroupMemberRepository;
import com.rohit.aitaskmanager.repository.TaskRepository;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class GroupMemberService {

    private final TaskRepository taskRepository;
    private final TaskGroupMemberRepository taskGroupMemberRepository;

    public GroupMemberService(TaskRepository taskRepository, TaskGroupMemberRepository taskGroupMemberRepository){
        this.taskRepository = taskRepository;
        this.taskGroupMemberRepository = taskGroupMemberRepository;
    }

    // View Assigned Tasks

    public Page<TaskResponseDTO> viewTasksAssignedToMember(Long userId,
                                                           Long groupId,
                                                           Status status,
                                                           Priority priority,
                                                           String category,
                                                           int page,
                                                           int size,
                                                           String sortBy,
                                                           String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Task> tasks = taskRepository.findByAssignedToIdAndTaskTypeAndGroupId(userId, TaskType.GROUP, groupId, pageable);

        return tasks.map(this::mapToResponse);
    }


    // Task Completion

    public TaskResponseDTO completeTask(Long userId, Long groupId, Long taskId){
        Task task = taskRepository.findByIdAndGroupIdAndTaskType(taskId, groupId, TaskType.GROUP);
        if(task == null)
            throw new TaskNotFoundException("Task not found with id: " + taskId);

        TaskGroupMember membership = getGroupMemberOrThrow(userId, groupId);


        if(membership.getGroupRole() != GroupRole.ADMIN && !task.getAssignedTo().getId().equals(userId)) {
            throw new UnauthorizedException("You are not authorized to complete this task");
        }

        task.setStatus(Status.DONE);
        task.setCompleted(true);

        return mapToResponse(taskRepository.save(task));
    }


    // Searching and Filtering

    public Page<TaskResponseDTO> filterTasks(Long userId,
                                             Long groupId,
                                             Status status,
                                             Priority priority,
                                             String category,
                                             int page,
                                             int size,
                                             String sortBy,
                                             String sortDir
    ){

        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Task> tasks;
        if (status != null && priority != null && category != null) {
            tasks = taskRepository.findByAssignedToIdAndStatusAndPriorityAndCategoryIgnoreCaseAndTaskTypeAndGroupId(userId, status, priority, category, TaskType.GROUP, groupId, pageable);
        } else if (status != null && priority != null) {
            tasks = taskRepository.findByAssignedToIdAndStatusAndPriorityAndTaskTypeAndGroupId(userId, status, priority, TaskType.GROUP, groupId, pageable);
        } else if (status != null && category != null) {
            tasks = taskRepository.findByAssignedToIdAndStatusAndCategoryIgnoreCaseAndTaskTypeAndGroupId(userId, status, category, TaskType.GROUP, groupId, pageable);
        } else if (priority != null && category != null) {
            tasks = taskRepository.findByAssignedToIdAndPriorityAndCategoryIgnoreCaseAndTaskTypeAndGroupId(userId, priority, category, TaskType.GROUP, groupId, pageable);
        } else if (status != null) {
            tasks = taskRepository.findByAssignedToIdAndStatusAndTaskTypeAndGroupId(userId, status, TaskType.GROUP, groupId, pageable);
        } else if (priority != null) {
            tasks = taskRepository.findByAssignedToIdAndPriorityAndTaskTypeAndGroupId(userId, priority, TaskType.GROUP, groupId, pageable);
        } else if (category != null) {
            tasks = taskRepository.findByAssignedToIdAndCategoryIgnoreCaseAndTaskTypeAndGroupId(userId, category, TaskType.GROUP, groupId, pageable);
        } else {
            tasks = taskRepository.findByAssignedToIdAndTaskTypeAndGroupId(userId, TaskType.GROUP, groupId, pageable);
        }

        return tasks
                .map(this::mapToResponse);
    }

    public Page<TaskResponseDTO> searchTasks(Long userId,
                                             Long groupId,
                                             String keyword,
                                             int page,
                                             int size,
                                             String sortBy,
                                             String sortDir
    ){

        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Task> titleDescMatches = taskRepository.findByAssignedToIdAndTitleOrDescriptionAndGroupId(userId, keyword, TaskType.GROUP, groupId, pageable);
        Page<Task> tagsMatches = taskRepository.findByAssignedToIdAndTagKeywordAndGroupId(userId, keyword, TaskType.GROUP, groupId, pageable);

        Set<Task> combinedSearch = new HashSet<>();
        combinedSearch.addAll(titleDescMatches.getContent());
        combinedSearch.addAll(tagsMatches.getContent());

        List<TaskResponseDTO> dtoList = combinedSearch
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        long finalCount = taskRepository.countByAssignedToIdAndTitleOrDescriptionAndGroupId(userId, keyword, groupId, TaskType.GROUP)
                + taskRepository.countByAssignedToIdAndTagKeywordAndGroupId(userId, keyword, groupId, TaskType.GROUP);

        return new PageImpl<>(dtoList, pageable, finalCount);
    }


    // Recents, Due-Date, Library

    public List<TaskResponseDTO> recentTasks(Long userId, int days) {
        LocalDateTime cutoff = LocalDateTime.now().minusDays(days);
        List<Task> tasks = taskRepository.findByAssignedToIdAndLastVisitedAfterAndTaskType(userId, cutoff, TaskType.GROUP);
        return tasks.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<TaskResponseDTO> getDueSoonTasks(Long userId, int days){
        LocalDate today = LocalDate.now();
        LocalDateTime startOfToday = LocalDateTime.now();
        LocalDateTime endOfToday = today.plusDays(days).atTime(LocalTime.MAX);

        return taskRepository.findByAssignedToIdAndDueDateBetweenAndTaskType(userId, startOfToday, endOfToday, TaskType.GROUP)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }


    public Page<TaskResponseDTO> getLibrary(Long userId,
                                            int page,
                                            int size,
                                            String sortBy,
                                            String sortDir
    ){
        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Task> tasks = taskRepository.findByAssignedToIdAndTaskType(userId, TaskType.GROUP, pageable);

        return tasks.map(this::mapToResponse);
    }


    // Utils

    private TaskResponseDTO mapToResponse(Task task) {
        return TaskResponseDTO.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .content(task.getContent())
                .status(task.getStatus())
                .priority(task.getPriority())
                .dueDate(task.getDueDate())
                .createdAt(task.getCreatedAt())
                .category(task.getCategory())
                .tags(task.getTags())
                .completed(task.getCompleted() != null ? task.getCompleted() : false)
                .userId(task.getCreatedBy() != null ? task.getCreatedBy().getId() : null)
                .assignedUserId(task.getAssignedTo() != null ? task.getAssignedTo().getId() : null)
                .groupId(task.getGroup() != null ? task.getGroup().getId() : null)
                .build();
    }


    private TaskGroupMember getGroupMemberOrThrow(Long userId, Long groupId){
        TaskGroupMember membership = taskGroupMemberRepository.findByUserIdAndGroupId(userId, groupId);

        if(membership == null)
            throw new UnauthorizedException("User not in group.");

        return membership;
    }

}
