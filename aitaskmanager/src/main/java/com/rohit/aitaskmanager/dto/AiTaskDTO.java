package com.rohit.aitaskmanager.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AiTaskDTO {
    private String title;
    private String description;
    private LocalDateTime dueDate;
    private String priority;
}
