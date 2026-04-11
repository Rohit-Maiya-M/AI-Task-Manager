package com.rohit.aitaskmanager.repository;


import com.rohit.aitaskmanager.models.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface GroupRepository extends JpaRepository<Group, Long> {
    @Query("SELECT g FROM Group g JOIN g.members m WHERE g.id = :groupId AND m.id = :userId")
    Group findByIdAndUserId(@Param("groupId") Long groupId, @Param("userId") Long userId);

    @Query("SELECT CASE WHEN COUNT(g) > 0 THEN TRUE ELSE FALSE END " +
            "FROM Group g JOIN g.members m " +
            "WHERE g.id = :groupId AND m.id = :userId")
    boolean existsByGroupIdAndUserId(@Param("groupId") Long groupId, @Param("userId") Long userId);
}
