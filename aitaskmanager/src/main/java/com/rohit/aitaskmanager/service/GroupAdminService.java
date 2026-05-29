package com.rohit.aitaskmanager.service;

import com.rohit.aitaskmanager.dto.GroupCreationRequestDTO;
import com.rohit.aitaskmanager.dto.GroupMemberRequestDTO;
import com.rohit.aitaskmanager.dto.GroupMemberResponseDTO;
import com.rohit.aitaskmanager.dto.TaskRequestDTO;
import com.rohit.aitaskmanager.dto.TaskResponseDTO;
import com.rohit.aitaskmanager.exception.*;
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
public class GroupAdminService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final GroupRepository groupRepository;
    private final TaskGroupMemberRepository taskGroupMemberRepository;

    public GroupAdminService(TaskRepository taskRepository,
                             UserRepository userRepository,
                             GroupRepository groupRepository,
                             TaskGroupMemberRepository taskGroupMemberRepository) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
        this.groupRepository = groupRepository;
        this.taskGroupMemberRepository = taskGroupMemberRepository;
    }


    // ---------------- AUTHENTICATION ----------------

    public void verifyGroupAdminAccess(Long userId, Long groupId, String password) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new GroupNotFoundException("Group not found with ID: " + groupId));

        if (password != null && !group.getPassword().equals(password)) {
            throw new InvalidCredentialsException("Invalid group administrative password.");
        }

        TaskGroupMember admin = getGroupMemberOrThrow(userId, groupId);

        if (admin.getGroupRole() != GroupRole.ADMIN) {
            throw new UnauthorizedException("Only admins can add members.");
        }
    }

    // ---------------- GROUP MANAGEMENT ----------------

    public GroupMemberResponseDTO createGroup(Long userId, GroupCreationRequestDTO dto) {
        Group group = mapToEntity(dto);
        Group newGroup = groupRepository.save(group);

        User user = getUserOrThrow(userId);

        TaskGroupMember admin = TaskGroupMember.builder()
                .group(newGroup)
                .user(user)
                .groupRole(GroupRole.ADMIN)
                .createdAt(LocalDateTime.now())
                .build();

        TaskGroupMember savedAdmin = taskGroupMemberRepository.save(admin);

        return GroupMemberResponseDTO.builder()
                .groupId(savedAdmin.getGroup().getId())
                .groupName(savedAdmin.getGroup().getName())
                .userId(savedAdmin.getUser().getId())
                .username(savedAdmin.getUser().getUsername())
                .role(savedAdmin.getGroupRole().name())
                .createdAt(savedAdmin.getCreatedAt())
                .build();
    }

    public GroupMemberResponseDTO createMember(Long userId, GroupMemberRequestDTO dto) {
        TaskGroupMember admin = getGroupMemberOrThrow(userId, dto.getGroupId());

        if (admin.getGroupRole() != GroupRole.ADMIN) {
            throw new UnauthorizedException("Only admins can add members.");
        }

        Group group = getGroupOrThrow(dto.getGroupId());
        User user = getUserOrThrow(dto.getUserId());

        TaskGroupMember membership = TaskGroupMember.builder()
                .group(group)
                .user(user)
                .groupRole(GroupRole.MEMBER)
                .createdAt(LocalDateTime.now())
                .build();

        TaskGroupMember savedMember = taskGroupMemberRepository.save(membership);

        return GroupMemberResponseDTO.builder()
                .groupId(savedMember.getGroup().getId())
                .groupName(savedMember.getGroup().getName())
                .userId(savedMember.getUser().getId())
                .username(savedMember.getUser().getUsername())
                .role(savedMember.getGroupRole().name())
                .createdAt(savedMember.getCreatedAt())
                .build();
    }

    public List<GroupMemberResponseDTO> viewMembers(Long userId, Long groupId) {
        verifyGroupAdminAccess(userId, groupId, null);

        List<TaskGroupMember> members = taskGroupMemberRepository.findByGroupId(groupId);

        return members.stream()
                .map(member -> GroupMemberResponseDTO.builder()
                        .groupId(member.getGroup().getId())
                        .groupName(member.getGroup().getName())
                        .userId(member.getUser().getId())
                        .username(member.getUser().getUsername()) // Assumes User entity contains a getUsername() method
                        .role(member.getGroupRole().name())
                        .createdAt(member.getCreatedAt())
                        .build())
                .collect(Collectors.toList());
    }

    public void deleteMember(Long userId, GroupMemberRequestDTO dto) {
        TaskGroupMember admin = getGroupMemberOrThrow(userId, dto.getGroupId());

        if (admin.getGroupRole() != GroupRole.ADMIN) {
            throw new UnauthorizedException("Only admins can delete members.");
        }

        TaskGroupMember membership = getGroupMemberOrThrow(dto.getUserId(), dto.getGroupId());
        taskGroupMemberRepository.delete(membership);
    }

    // ---------------- TASK MANAGEMENT ----------------
    // (Your existing createTaskForGroup, createTaskForMember, editTask, assignTask, completeTask remain the same)


    public TaskResponseDTO createTaskForGroup(Long userId, Long groupId, TaskRequestDTO dto){
        TaskGroupMember membership = getGroupMemberOrThrow(userId, groupId);

        if(membership.getGroupRole() != GroupRole.ADMIN){
            throw new UnauthorizedException("Only admins can create Tasks");
        }

        Group group = membership.getGroup();
        Task newTask = mapToEntity(userId, dto);
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

        Group group = assignedUserMembership.getGroup();
        Task newTask = mapToEntity(userId, dto);
        newTask.setAssignedTo(assignedUserMembership.getUser());
        newTask.setGroup(group);

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
        Task task = taskRepository.findByIdAndGroupIdAndTaskType(taskId, groupId, TaskType.GROUP);

        if(task == null)
            throw new TaskNotFoundException("Task not found with id: " + taskId);

        TaskGroupMember membership = getGroupMemberOrThrow(userId, groupId);


        if(membership.getGroupRole() != GroupRole.ADMIN && !task.getAssignedTo().getId().equals(userId)) {
            throw new UnauthorizedException("You are not authorized to complete this task");
        }
        task.setStatus(Status.DONE);
        task.setCompleted(true);
        task.setLastVisited(LocalDateTime.now());
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


    // ---------------- FILTERING & SEARCH ----------------
    // (Your existing filterTasks and searchTasks remain the same) AccessDenied

    public Page<TaskResponseDTO> filterTasks(Long userId,
                                             Long groupId,
                                             Status status,
                                             Priority priority,
                                             String category,
                                             int page,
                                             int size,
                                             String sortBy,
                                             String sortDir) {

        TaskGroupMember adminUserMembership = getGroupMemberOrThrow(userId, groupId);

        if (adminUserMembership.getGroupRole() != GroupRole.ADMIN) {
            throw new UnauthorizedException("Only admins can filter Tasks");
        }

        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Task> tasks;
        if (status != null && priority != null && category != null) {
            tasks = taskRepository.findByGroupIdAndStatusAndPriorityAndCategoryIgnoreCaseAndTaskType(groupId, status, priority, category, TaskType.GROUP, pageable);
        } else if (status != null && priority != null) {
            tasks = taskRepository.findByGroupIdAndStatusAndPriorityAndTaskType(groupId, status, priority, TaskType.GROUP, pageable);
        } else if (status != null && category != null) {
            tasks = taskRepository.findByGroupIdAndStatusAndCategoryIgnoreCaseAndTaskType(groupId, status, category, TaskType.GROUP, pageable);
        } else if (priority != null && category != null) {
            tasks = taskRepository.findByGroupIdAndPriorityAndCategoryIgnoreCaseAndTaskType(groupId, priority, category, TaskType.GROUP, pageable);
        } else if (status != null) {
            tasks = taskRepository.findByGroupIdAndStatusAndTaskType(groupId, status, TaskType.GROUP, pageable);
        } else if (priority != null) {
            tasks = taskRepository.findByGroupIdAndPriorityAndTaskType(groupId, priority, TaskType.GROUP, pageable);
        } else if (category != null) {
            tasks = taskRepository.findByGroupIdAndCategoryIgnoreCaseAndTaskType(groupId, category, TaskType.GROUP, pageable);
        } else {
            tasks = taskRepository.findByGroupIdAndTaskType(groupId, TaskType.GROUP, pageable);
        }

        return tasks.map(this::mapToResponse);
    }

    public Page<TaskResponseDTO> searchTasks(Long userId,
                                             Long groupId,
                                             String keyword,
                                             int page,
                                             int size,
                                             String sortBy,
                                             String sortDir) {

        TaskGroupMember adminUserMembership = getGroupMemberOrThrow(userId, groupId);

        if (adminUserMembership.getGroupRole() != GroupRole.ADMIN) {
            throw new UnauthorizedException("Only admins can search Tasks");
        }

        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Task> titleDescMatches = taskRepository.findByGroupIdAndTitleOrDescription(groupId, keyword, TaskType.GROUP, pageable);
        Page<Task> tagsMatches = taskRepository.findByGroupIdAndTagKeyword(groupId, keyword, TaskType.GROUP, pageable);

        Set<Task> combinedSearch = new HashSet<>();
        combinedSearch.addAll(titleDescMatches.getContent());
        combinedSearch.addAll(tagsMatches.getContent());

        List<TaskResponseDTO> dtoList = combinedSearch.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        long finalCount = taskRepository.countByGroupIdAndTitleOrDescription(groupId, keyword, TaskType.GROUP)
                + taskRepository.countByGroupIdAndTagKeyword(groupId, keyword, TaskType.GROUP);

        return new PageImpl<>(dtoList, pageable, finalCount);
    }

    // ---------------- UTILS ----------------

    private Group mapToEntity(GroupCreationRequestDTO dto) {
        return Group.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .password(dto.getPassword())
                .build();
    }

    private Task mapToEntity(Long userId, TaskRequestDTO dto){
        User user = getUserOrThrow(userId);


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

    private Task getTaskOrThrow(Long groupId, Long taskId){
        Task task = taskRepository.findByIdAndGroupIdAndTaskType(taskId, groupId, TaskType.GROUP);

        if(task == null)
            throw new TaskNotFoundException("Task not found!");

        return task;
    }

    private User getUserOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + userId));
    }

    private Group getGroupOrThrow(Long groupId) {
        return groupRepository.findById(groupId)
                .orElseThrow(() -> new GroupNotFoundException("Group not found with id: " + groupId));
    }

    private TaskGroupMember getGroupMemberOrThrow(Long userId, Long groupId) {
        TaskGroupMember membership = taskGroupMemberRepository.findByUserIdAndGroupId(userId, groupId);
        if (membership == null) {
            throw new UnauthorizedException("User not in group.");
        }
        return membership;
    }

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
}
