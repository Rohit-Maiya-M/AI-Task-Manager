package com.rohit.aitaskmanager.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GroupMemberResponseDTO {
    private Long groupId;
    private Long userId;
    private String groupName;
    private String username;
    private String role;
    private LocalDateTime createdAt;
}
