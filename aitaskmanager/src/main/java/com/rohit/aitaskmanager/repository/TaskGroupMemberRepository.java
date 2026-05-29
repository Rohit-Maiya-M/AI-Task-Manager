package com.rohit.aitaskmanager.repository;

import com.rohit.aitaskmanager.models.GroupRole;
import com.rohit.aitaskmanager.models.TaskGroupMember;
import com.rohit.aitaskmanager.models.TaskType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TaskGroupMemberRepository extends JpaRepository<TaskGroupMember, Long> {
    TaskGroupMember findByUserIdAndGroupId(Long userId, Long groupId);
    List<TaskGroupMember> findByGroupId(Long groupId);
}
