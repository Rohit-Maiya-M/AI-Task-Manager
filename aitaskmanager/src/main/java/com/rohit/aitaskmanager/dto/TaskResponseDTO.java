package com.rohit.aitaskmanager.dto;

import com.rohit.aitaskmanager.models.Priority;
import com.rohit.aitaskmanager.models.Status;
import com.rohit.aitaskmanager.models.TaskType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TaskResponseDTO {
    private Long id;

    private String title;
    private String description;
    private String content;

    private Status status;
    private Priority priority;
    private String category;
    private List<String> tags;

    private LocalDateTime dueDate;
    private Boolean completed;

    private LocalDateTime createdAt;
    private LocalDateTime lastVisited;

    private Long userId;
    private Long groupId;
    private Long assignedUserId;

    private TaskType taskType;
}
