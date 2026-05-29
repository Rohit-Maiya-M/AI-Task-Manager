package com.rohit.aitaskmanager.dto;


import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GroupCreationRequestDTO {
    @NotBlank(message = "Name is required")
    private String name;

    private String description;

    @NotBlank(message = "Password is required!")
    private String password;
}
