package com.rohit.aitaskmanager.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class GroupLoginRequestDTO {
    @NotNull(message = "Group ID is required")
    private Long groupId;

    @NotBlank(message = "Password is required")
    private String password;
}