package com.rohit.aitaskmanager.service;


import com.rohit.aitaskmanager.dto.GroupMemberDTO;
import com.rohit.aitaskmanager.dto.TaskRequestDTO;
import com.rohit.aitaskmanager.dto.TaskResponseDTO;
import com.rohit.aitaskmanager.exception.GroupNotFoundException;
import com.rohit.aitaskmanager.exception.TaskNotFoundException;
import com.rohit.aitaskmanager.exception.UnauthorizedException;
import com.rohit.aitaskmanager.exception.UsernameNotFoundException;
import com.rohit.aitaskmanager.models.*;
import com.rohit.aitaskmanager.repository.GroupRepository;
import com.rohit.aitaskmanager.repository.TaskGroupMemberRepository;
import com.rohit.aitaskmanager.repository.TaskRepository;
import com.rohit.aitaskmanager.repository.UserRepository;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class TaskGroupAdminService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final GroupRepository groupRepository;
    private final TaskGroupMemberRepository taskGroupMemberRepository;

    public TaskGroupAdminService(TaskRepository taskRepository, UserRepository userRepository, GroupRepository groupRepository, TaskGroupMemberRepository taskGroupMemberRepository){
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
        this.groupRepository = groupRepository;
        this.taskGroupMemberRepository = taskGroupMemberRepository;
    }

    // Task

    public TaskResponseDTO createTaskForGroup(Long userId, Long groupId, TaskRequestDTO dto){
        TaskGroupMember membership = getGroupMemberOrThrow(userId, groupId);

        if(membership.getGroupRole() != GroupRole.ADMIN){
            throw new UnauthorizedException("Only admins can create Tasks");
        }

        Group group = membership.getGroup();
        Task newTask = mapToEntity(dto);
        newTask.setGroup(group);

        Task savedTask = taskRepository.save(newTask);
        return mapToResponse(savedTask);
    }

    public TaskResponseDTO createTaskForMember(Long userId, Long assignedUserId, Long groupId, TaskRequestDTO dto){
        TaskGroupMember assignedUserMembership = getGroupMemberOrThrow(assignedUserId, groupId);
        TaskGroupMember adminUserMembership = getGroupMemberOrThrow(userId, groupId);

        if(adminUserMembership.getGroupRole() != GroupRole.ADMIN){
            throw new UnauthorizedException("Only admins can create Tasks");
        }

        Task newTask = mapToEntity(dto);
        newTask.setAssignedTo(assignedUserMembership.getUser());

        Task savedTask = taskRepository.save(newTask);
        return mapToResponse(savedTask);
    }

    public TaskResponseDTO editTaskForGroup(Long userId, Long groupId, Long taskId, TaskRequestDTO dto){
        TaskGroupMember adminUserMembership = getGroupMemberOrThrow(userId, groupId);


        if(adminUserMembership.getGroupRole() != GroupRole.ADMIN){
            throw new UnauthorizedException("Only admins can create Tasks");
        }

        Task updateTask = getTaskOrThrow(groupId, taskId);

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

    public TaskResponseDTO editTaskForMember(Long userId, Long assignUserId, Long groupId, Long taskId, TaskRequestDTO dto){

        TaskGroupMember adminUserMembership = getGroupMemberOrThrow(userId, groupId);
        TaskGroupMember assignUserMembership = getGroupMemberOrThrow(assignUserId, groupId);


        if(adminUserMembership.getGroupRole() != GroupRole.ADMIN){
            throw new UnauthorizedException("Only admins can create Tasks");
        }

        Task updateTask = getTaskOrThrow(groupId, taskId);

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
        updateTask.setAssignedTo(assignUserMembership.getUser());

        Task editedTask = taskRepository.save(updateTask);
        return mapToResponse(editedTask);
    }


    public TaskResponseDTO completeTask(Long userId, Long groupId, Long taskId){
        Task task = taskRepository.findByIdAndGroupIdAndTaskType(taskId, groupId, TaskType.GROUP)
                .orElseThrow(() -> new TaskNotFoundException("Task not found with id: " + taskId));

        if(!task.getAssignedTo().getId().equals(userId)){
            throw new UnauthorizedException("You are not assigned to this task");
        }

        task.setStatus(Status.DONE);
        task.setCompleted(true);

        return mapToResponse(taskRepository.save(task));
    }

    public TaskResponseDTO assignTaskForMember(Long userId, Long assignedUserId, Long groupId, Long taskId){

        TaskGroupMember adminUserMembership = getGroupMemberOrThrow(userId, groupId);
        TaskGroupMember assignedUserMembership = getGroupMemberOrThrow(assignedUserId, groupId);

        if(adminUserMembership.getGroupRole() != GroupRole.ADMIN){
            throw new UnauthorizedException("Only admins can create Tasks");
        }

        Task task = getTaskOrThrow(groupId, taskId);
        task.setAssignedTo(assignedUserMembership.getUser());

        Task assignedTask = taskRepository.save(task);
        return mapToResponse(assignedTask);
    }




    // Members

    public TaskGroupMember createMember(GroupMemberDTO dto){
        TaskGroupMember membership = mapToEntity(dto);
        membership.setGroupRole(GroupRole.MEMBER);
        return taskGroupMemberRepository.save(membership);
    }

    public void deleteMember(GroupMemberDTO dto){
        TaskGroupMember membership = getGroupMemberOrThrow(dto.getUserId(), dto.getGroupId());
        taskGroupMemberRepository.delete(membership);
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

        TaskGroupMember adminUserMembership = getGroupMemberOrThrow(userId, groupId);


        if(adminUserMembership.getGroupRole() != GroupRole.ADMIN){
            throw new UnauthorizedException("Only admins can filter Tasks");
        }

        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Task> tasks;
        if (status != null && priority != null && category != null) {
            tasks = taskRepository.findByCreatedByIdAndStatusAndPriorityAndCategoryIgnoreCaseAndTaskTypeAndGroupId(userId, status, priority, category, TaskType.GROUP, groupId, pageable);
        } else if (status != null && priority != null) {
            tasks = taskRepository.findByCreatedByIdAndStatusAndPriorityAndTaskTypeAndGroupId(userId, status, priority, TaskType.GROUP, groupId, pageable);
        } else if (status != null && category != null) {
            tasks = taskRepository.findByCreatedByIdAndStatusAndCategoryIgnoreCaseAndTaskTypeAndGroupId(userId, status, category, TaskType.GROUP, groupId, pageable);
        } else if (priority != null && category != null) {
            tasks = taskRepository.findByCreatedByIdAndPriorityAndCategoryIgnoreCaseAndTaskTypeAndGroupId(userId, priority, category, TaskType.GROUP, groupId, pageable);
        } else if (status != null) {
            tasks = taskRepository.findByCreatedByIdAndStatusAndTaskTypeAndGroupId(userId, status, TaskType.GROUP, groupId, pageable);
        } else if (priority != null) {
            tasks = taskRepository.findByCreatedByIdAndPriorityAndTaskTypeAndGroupId(userId, priority, TaskType.GROUP, groupId, pageable);
        } else if (category != null) {
            tasks = taskRepository.findByCreatedByIdAndCategoryIgnoreCaseAndTaskTypeAndGroupId(userId, category, TaskType.GROUP, groupId, pageable);
        } else {
            tasks = taskRepository.findByCreatedByIdAndTaskTypeAndGroupId(userId, TaskType.GROUP, groupId, pageable);
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

        TaskGroupMember adminUserMembership = getGroupMemberOrThrow(userId, groupId);


        if(adminUserMembership.getGroupRole() != GroupRole.ADMIN){
            throw new UnauthorizedException("Only admins can search Tasks");
        }

        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Task> titleDescMatches = taskRepository.findByCreatedByIdAndTitleOrDescriptionAndGroupId(userId, keyword, TaskType.GROUP, groupId, pageable);
        Page<Task> tagsMatches = taskRepository.findByCreatedByIdAndTagKeywordAndGroupId(userId, keyword, TaskType.GROUP, groupId, pageable);

        Set<Task> combinedSearch = new HashSet<>();
        combinedSearch.addAll(titleDescMatches.getContent());
        combinedSearch.addAll(tagsMatches.getContent());

        List<TaskResponseDTO> dtoList = combinedSearch
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        long finalCount = taskRepository.countByCreatedByIdAndTitleOrDescriptionAndGroupId(userId, keyword, groupId, TaskType.GROUP)
                + taskRepository.countByCreatedByIdAndTagKeywordAndGroupId(userId, keyword, groupId, TaskType.GROUP);

        return new PageImpl<>(dtoList, pageable, finalCount);
    }







    // Utils

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
                .taskType(TaskType.GROUP)
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

    private TaskGroupMember mapToEntity(GroupMemberDTO dto){
        Group group = getGroupOrThrow(dto.getUserId(), dto.getGroupId());
        User user = getUserOrThrow(dto.getUserId());
        return TaskGroupMember.builder()
                .group(group)
                .user(user)
                .build();
    }

    private User getUserOrThrow(Long userId){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("Username not found with id: " + userId));
        return user;

    }

    private User getAssignedUserOrThrow(Long userId){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("Username not found with id: " + userId));
        return user;
    }

    private Task getTaskOrThrow(Long groupId, Long taskId){
        Task task = taskRepository.findByIdAndGroupIdAndTaskType(taskId, groupId, TaskType.GROUP);

        if(task == null)
            throw new TaskNotFoundException("Task not found with id: "+ taskId);

        return task;
    }

    private Group getGroupOrThrow(Long userId, Long groupId){
        Group group = groupRepository.findByIdAndMemberId(groupId, userId);

        if(group == null)
            throw new GroupNotFoundException("Group not found with id: " + groupId);

        return group;
    }

    private TaskGroupMember getGroupMemberOrThrow(Long userId, Long groupId){
        TaskGroupMember membership = taskGroupMemberRepository.findByUserIdAndGroupId(userId, groupId)
                .orElseThrow(() -> new UnauthorizedException("User not in group."));
        return membership;
    }


}
