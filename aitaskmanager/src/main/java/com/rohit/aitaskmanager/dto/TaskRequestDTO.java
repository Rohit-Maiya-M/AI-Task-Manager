package com.rohit.aitaskmanager.dto;


import com.rohit.aitaskmanager.models.Priority;
import com.rohit.aitaskmanager.models.Status;
import com.rohit.aitaskmanager.models.TaskType;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
public class TaskRequestDTO {

    @NotBlank(message = "Title is required.")
    @Size(max = 300, message = "Title should be under 300 characters.")
    private String title;

    @Size(max = 500, message = "Description must be under 500 characters")
    private String description;

    private String content;

    private Status status;

    private Priority priority;

    private String category;

    private List<String> tags;

    @Future(message = "Due date must be in the future")
    private LocalDateTime dueDate;

    private Boolean completed;

    private Long userId;

    private Long groupId;

    @NotNull(message = "Task type is required")
    private TaskType taskType;

    private Long assignedUserId;

}
