package com.rohit.aitaskmanager.service;


import com.rohit.aitaskmanager.dto.TaskRequestDTO;
import com.rohit.aitaskmanager.dto.TaskResponseDTO;
import com.rohit.aitaskmanager.exception.TaskNotFoundException;
import com.rohit.aitaskmanager.exception.UsernameNotFoundException;
import com.rohit.aitaskmanager.models.*;
import com.rohit.aitaskmanager.repository.TaskRepository;
import com.rohit.aitaskmanager.repository.UserRepository;
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
public class TaskPersonalService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    public TaskPersonalService(TaskRepository taskRepository, UserRepository userRepository){
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }

    // Create, Edit, Delete, Filter, Search, Recent, DueDate, Library

    public TaskResponseDTO createTask(TaskRequestDTO dto){
        Task newTask = mapToEntity(dto);
        Task createdtask = taskRepository.save(newTask);
        return mapToResponse(createdtask);
    }

    public TaskResponseDTO editTask(Long userId, Long taskId, TaskRequestDTO dto){

        Task updateTask = getTaskOrThrow(userId, taskId);

        updateTask.setTitle(dto.getTitle());
        updateTask.setDescription(dto.getDescription());
        updateTask.setContent(dto.getContent());
        updateTask.setStatus(dto.getStatus() != null ? dto.getStatus() : Status.TODO);
        updateTask.setPriority(dto.getPriority() != null ? dto.getPriority() : Priority.LOW);
        updateTask.setCategory(dto.getCategory());
        updateTask.setTags(dto.getTags());
        updateTask.setDueDate(dto.getDueDate());
        updateTask.setCompleted(dto.getCompleted() != null ? dto.getCompleted() : false);
        updateTask.setLastVisited(LocalDateTime.now());

        Task editedTask = taskRepository.save(updateTask);
        return mapToResponse(editedTask);
    }

    public void deleteTask(Long userId, Long taskId){
        Task deleteTask = getTaskOrThrow(userId, taskId);
        taskRepository.delete(deleteTask);
    }

    public Page<TaskResponseDTO> filterTasks(Long userId,
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
            tasks = taskRepository.findByCreatedByIdAndStatusAndPriorityAndCategoryIgnoreCaseAndTaskType(userId, status, priority, category, TaskType.PERSONAL, pageable);
        } else if (status != null && priority != null) {
            tasks = taskRepository.findByCreatedByIdAndStatusAndPriorityAndTaskType(userId, status, priority, TaskType.PERSONAL, pageable);
        } else if (status != null && category != null) {
            tasks = taskRepository.findByCreatedByIdAndStatusAndCategoryIgnoreCaseAndTaskType(userId, status, category, TaskType.PERSONAL, pageable);
        } else if (priority != null && category != null) {
            tasks = taskRepository.findByCreatedByIdAndPriorityAndCategoryIgnoreCaseAndTaskType(userId, priority, category, TaskType.PERSONAL, pageable);
        } else if (status != null) {
            tasks = taskRepository.findByCreatedByIdAndStatusAndTaskType(userId, status, TaskType.PERSONAL, pageable);
        } else if (priority != null) {
            tasks = taskRepository.findByCreatedByIdAndPriorityAndTaskType(userId, priority, TaskType.PERSONAL, pageable);
        } else if (category != null) {
            tasks = taskRepository.findByCreatedByIdAndCategoryIgnoreCaseAndTaskType(userId, category, TaskType.PERSONAL, pageable);
        } else {
            tasks = taskRepository.findByCreatedByIdAndTaskType(userId, TaskType.PERSONAL, pageable);
        }

        return tasks
                .map(this::mapToResponse);
    }

    public Page<TaskResponseDTO> searchTasks(Long userId,
                                             String keyword,
                                             int page,
                                             int size,
                                             String sortBy,
                                             String sortDir
        ){
        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Task> titleDescMatches = taskRepository.findByCreatedByIdAndTitleOrDescription(userId, keyword, TaskType.PERSONAL, pageable);
        Page<Task> tagsMatches = taskRepository.findByCreatedByIdAndTagKeyword(userId, keyword, TaskType.PERSONAL, pageable);

        Set<Task> combinedSearch = new HashSet<>();
        combinedSearch.addAll(titleDescMatches.getContent());
        combinedSearch.addAll(tagsMatches.getContent());

        List<TaskResponseDTO> dtoList = combinedSearch
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        long finalCount = taskRepository.countByCreatedByIdAndTitleOrDescription(userId, keyword, TaskType.PERSONAL)
                + taskRepository.countByCreatedByIdAndTagKeyword(userId, keyword, TaskType.PERSONAL);

        return new PageImpl<>(dtoList, pageable, finalCount);
    }

    public List<TaskResponseDTO> recentTasks(Long userId, int days) {
        LocalDateTime cutoff = LocalDateTime.now().minusDays(days);
        List<Task> tasks = taskRepository.findByCreatedByIdAndLastVisitedAfterAndTaskType(userId, cutoff, TaskType.PERSONAL);
        return tasks.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<TaskResponseDTO> getDueSoonTasks(Long userId, int days){
        LocalDate today = LocalDate.now();
        LocalDateTime startOfToday = LocalDateTime.now();
        LocalDateTime endOfToday = today.plusDays(days).atTime(LocalTime.MAX);

        return taskRepository.findByCreatedByIdAndDueDateBetweenAndTaskType(userId, startOfToday, endOfToday, TaskType.PERSONAL)
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

        Page<Task> tasks = taskRepository.findByCreatedByIdAndTaskType(userId, TaskType.PERSONAL, pageable);

        return tasks.map(this::mapToResponse);
    }


    private Task mapToEntity(TaskRequestDTO dto){
        User user = getUserOrThrow(dto.getUserId());

        return Task.builder()
                .title(dto.getTitle())
                .description(dto.getDescription())
                .content(dto.getContent())
                .status(dto.getStatus() != null ? dto.getStatus() : Status.TODO)
                .priority(dto.getPriority() != null ? dto.getPriority() : Priority.LOW)
                .category(dto.getCategory())
                .tags(dto.getTags())
                .createdAt(LocalDateTime.now())
                .dueDate(dto.getDueDate())
                .completed(dto.getCompleted() != null ? dto.getCompleted() : false)
                .createdBy(user)
                .lastVisited(LocalDateTime.now())
                .build();
    }

    private TaskResponseDTO mapToResponse(Task task) {
        return TaskResponseDTO.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
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

    private User getUserOrThrow(Long userId){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("Username not found with id: " + userId));
        return user;

    }

    private Task getTaskOrThrow(Long userId, Long taskId){
        Task task = taskRepository.findByIdAndCreatedByIdAndTaskType(taskId, userId, TaskType.PERSONAL);

        if(task == null)
            throw new TaskNotFoundException("Task not found with id: "+ taskId);

        return task;
    }


}
