package com.rohit.aitaskmanager.repository;

import com.rohit.aitaskmanager.models.Priority;
import com.rohit.aitaskmanager.models.Status;
import com.rohit.aitaskmanager.models.Task;
import com.rohit.aitaskmanager.models.TaskType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TaskRepository extends JpaRepository<Task, Long> {

    // Search Tasks
    @Query("SELECT t FROM Task t WHERE t.createdBy.id=:userid AND t.taskType=:taskType AND " +
            "(LOWER(t.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(t.description) LIKE LOWER(CONCAT('%', :keyword, '%')))"
    )
    Page<Task> findByCreatedByIdAndTitleOrDescription(@Param("userid") Long userId,
                                                 @Param("keyword") String keyword,
                                                 @Param("taskType") TaskType taskType,
                                                 Pageable pageable
    );

    @Query("SELECT t FROM Task t JOIN t.tags tag WHERE LOWER(tag) LIKE LOWER(CONCAT('%', :keyword, '%')) AND t.createdBy.id = :userId AND t.taskType=:taskType ")
    Page<Task> findByCreatedByIdAndTagKeyword(@Param("userId") Long userId, @Param("keyword") String keyword, @Param("taskType") TaskType taskType, Pageable pageable);

    @Query("SELECT COUNT(t) FROM Task t JOIN t.tags tag WHERE LOWER(tag) LIKE LOWER(CONCAT('%', :keyword, '%')) AND t.createdBy.id = :userId AND t.taskType=:taskType ")
    Long countByCreatedByIdAndTagKeyword(@Param("userId") Long userId, @Param("keyword") String keyword, @Param("taskType") TaskType taskType);

    @Query("SELECT COUNT(t) FROM Task t WHERE t.createdBy.id=:userid AND t.taskType=:taskType AND " +
            "(LOWER(t.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(t.description) LIKE LOWER(CONCAT('%', :keyword, '%')))"
    )
    Long countByCreatedByIdAndTitleOrDescription(@Param("userid") Long userId,
                                                      @Param("keyword") String keyword,
                                                 @Param("taskType") TaskType taskType
    );

    @Query("SELECT DISTINCT tag FROM Task t JOIN t.tags tag WHERE t.createdBy.id=:userId")
    List<String> findByCreatedByIdAndAllTags(@Param("userId") Long userId);

    Task findByIdAndCreatedById(Long taskId, Long userId);

// findByIdAndGroupIdAndTaskType

    // Filter by status
    Page<Task> findByCreatedByIdAndStatusAndTaskType(Long userId, Status status, TaskType taskType, Pageable pageable);

    // Filter by priority
    Page<Task> findByCreatedByIdAndPriorityAndTaskType(Long userId, Priority priority, TaskType taskType, Pageable pageable);

    // Filter by category (case-insensitive)
    Page<Task> findByCreatedByIdAndCategoryIgnoreCaseAndTaskType(Long userId, String category, TaskType taskType, Pageable pageable);

    // Combined filter (status + priority + category)
    Page<Task> findByCreatedByIdAndStatusAndPriorityAndCategoryIgnoreCaseAndTaskType(Long userId, Status status, Priority priority, String category, TaskType taskType, Pageable pageable);

    Page<Task> findByCreatedByIdAndStatusAndPriorityAndTaskType(Long userId, Status status, Priority priority, TaskType taskType, Pageable pageable);
    Page<Task> findByCreatedByIdAndStatusAndCategoryIgnoreCaseAndTaskType(Long userId, Status status, String category, TaskType taskType, Pageable pageable);
    Page<Task> findByCreatedByIdAndPriorityAndCategoryIgnoreCaseAndTaskType(Long userId, Priority priority, String category, TaskType taskType, Pageable pageable);

    // Recent Tasks
    List<Task> findByCreatedByIdAndLastVisitedAfterAndTaskType(Long userId, LocalDateTime cutoff, TaskType taskType);
    List<Task> findByAssignedToIdAndLastVisitedAfterAndTaskType(Long userId, LocalDateTime cutoff, TaskType taskType);


    // Due Date
    List<Task> findByCreatedByIdAndDueDateBetweenAndTaskType(Long userId, LocalDateTime start, LocalDateTime end, TaskType taskType);
    List<Task> findByAssignedToIdAndDueDateBetweenAndTaskType(Long userId, LocalDateTime start, LocalDateTime end, TaskType taskType);

    // Common
    Task findByIdAndCreatedByIdAndTaskType(Long taskId, Long userId, TaskType taskType);
    Page<Task> findByCreatedByIdAndTaskType(Long userId, TaskType taskType, Pageable pageable);
    Page<Task> findByAssignedToIdAndTaskType(Long userId, TaskType taskType, Pageable pageable);

    Task findByIdAndGroupIdAndTaskType(Long taskId, Long groupId, TaskType taskType);
    // For Group

        // Search Tasks
        @Query("SELECT t FROM Task t WHERE t.createdBy.id=:userid AND t.taskType=:taskType AND t.group.id=:groupId AND " +
                "(LOWER(t.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
                "LOWER(t.description) LIKE LOWER(CONCAT('%', :keyword, '%')))"
        )
        Page<Task> findByCreatedByIdAndTitleOrDescriptionAndGroupId(@Param("userid") Long userId,
                                                          @Param("keyword") String keyword,
                                                          @Param("taskType") TaskType taskType,
                                                                    @Param("groupId") Long groupId,
                                                          Pageable pageable
        );

        @Query("SELECT t FROM Task t JOIN t.tags tag WHERE LOWER(tag) LIKE LOWER(CONCAT('%', :keyword, '%')) AND t.createdBy.id = :userId AND t.group.id=:groupId AND t.taskType=:taskType ")
        Page<Task> findByCreatedByIdAndTagKeywordAndGroupId(@Param("userId") Long userId, @Param("keyword") String keyword, @Param("taskType") TaskType taskType, @Param("groupId") Long groupId, Pageable pageable);

        @Query("SELECT COUNT(t) FROM Task t JOIN t.tags tag WHERE LOWER(tag) LIKE LOWER(CONCAT('%', :keyword, '%')) AND t.createdBy.id = :userId AND t.group.id=:groupId AND t.taskType=:taskType ")
        Long countByCreatedByIdAndTagKeywordAndGroupId(@Param("userId") Long userId, @Param("keyword") String keyword, @Param("groupId") Long groupId, @Param("taskType") TaskType taskType);

        @Query("SELECT COUNT(t) FROM Task t WHERE t.createdBy.id=:userid AND t.group.id=:groupId AND t.taskType=:taskType AND " +
                "(LOWER(t.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
                "LOWER(t.description) LIKE LOWER(CONCAT('%', :keyword, '%')))"
        )
        Long countByCreatedByIdAndTitleOrDescriptionAndGroupId(@Param("userid") Long userId,
                                                     @Param("keyword") String keyword,
                                                               @Param("groupId") Long groupId,
                                                     @Param("taskType") TaskType taskType
        );

        // Filter by status
        Page<Task> findByCreatedByIdAndStatusAndTaskTypeAndGroupId(Long userId, Status status, TaskType taskType, Long groupId, Pageable pageable);

        // Filter by priority
        Page<Task> findByCreatedByIdAndPriorityAndTaskTypeAndGroupId(Long userId, Priority priority, TaskType taskType, Long groupId, Pageable pageable);

        // Filter by category (case-insensitive)
        Page<Task> findByCreatedByIdAndCategoryIgnoreCaseAndTaskTypeAndGroupId(Long userId, String category, TaskType taskType, Long groupId, Pageable pageable);

        // Combined filter (status + priority + category)
        Page<Task> findByCreatedByIdAndStatusAndPriorityAndCategoryIgnoreCaseAndTaskTypeAndGroupId(Long userId, Status status, Priority priority, String category, TaskType taskType, Long groupId, Pageable pageable);

        Page<Task> findByCreatedByIdAndStatusAndPriorityAndTaskTypeAndGroupId(Long userId, Status status, Priority priority, TaskType taskType, Long groupId, Pageable pageable);
        Page<Task> findByCreatedByIdAndStatusAndCategoryIgnoreCaseAndTaskTypeAndGroupId(Long userId, Status status, String category, TaskType taskType, Long groupId, Pageable pageable);
        Page<Task> findByCreatedByIdAndPriorityAndCategoryIgnoreCaseAndTaskTypeAndGroupId(Long userId, Priority priority, String category, TaskType taskType, Long groupId, Pageable pageable);

        // Recent Tasks
        List<Task> findByCreatedByIdAndLastVisitedAfterAndTaskTypeAndGroupId(Long userId, LocalDateTime cutoff, TaskType taskType, Long groupId);

        // Due Date
        List<Task> findByCreatedByIdAndDueDateBetweenAndTaskTypeAndGroupId(Long userId, LocalDateTime start, LocalDateTime end, TaskType taskType, Long groupId);

        // Common
        Task findByIdAndCreatedByIdAndTaskTypeAndGroupId(Long taskId, Long userId, TaskType taskType, Long groupId);
        Page<Task> findByCreatedByIdAndTaskTypeAndGroupId(Long userId, TaskType taskType, Long groupId, Pageable pageable);

    // For Assigned Users

        // Search Tasks
        @Query("SELECT t FROM Task t WHERE t.assignedTo.id=:userid AND t.taskType=:taskType AND t.group.id=:groupId AND " +
                "(LOWER(t.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
                "LOWER(t.description) LIKE LOWER(CONCAT('%', :keyword, '%')))"
        )
        Page<Task> findByAssignedToIdAndTitleOrDescriptionAndGroupId(@Param("userid") Long userId,
                                                                    @Param("keyword") String keyword,
                                                                    @Param("taskType") TaskType taskType,
                                                                    @Param("groupId") Long groupId,
                                                                    Pageable pageable
        );

        @Query("SELECT t FROM Task t JOIN t.tags tag WHERE LOWER(tag) LIKE LOWER(CONCAT('%', :keyword, '%')) AND t.assignedTo.id = :userId AND t.group.id=:groupId AND t.taskType=:taskType ")
        Page<Task> findByAssignedToIdAndTagKeywordAndGroupId(@Param("userId") Long userId, @Param("keyword") String keyword, @Param("taskType") TaskType taskType, @Param("groupId") Long groupId, Pageable pageable);

        @Query("SELECT COUNT(t) FROM Task t JOIN t.tags tag WHERE LOWER(tag) LIKE LOWER(CONCAT('%', :keyword, '%')) AND t.assignedTo.id = :userId AND t.group.id=:groupId AND t.taskType=:taskType ")
        Long countByAssignedToIdAndTagKeywordAndGroupId(@Param("userId") Long userId, @Param("keyword") String keyword, @Param("groupId") Long groupId, @Param("taskType") TaskType taskType);

        @Query("SELECT COUNT(t) FROM Task t WHERE t.assignedTo.id=:userid AND t.group.id=:groupId AND t.taskType=:taskType AND " +
                "(LOWER(t.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
                "LOWER(t.description) LIKE LOWER(CONCAT('%', :keyword, '%')))"
        )
        Long countByAssignedToIdAndTitleOrDescriptionAndGroupId(@Param("userid") Long userId,
                                                               @Param("keyword") String keyword,
                                                               @Param("groupId") Long groupId,
                                                               @Param("taskType") TaskType taskType
        );


        // Filter by status
        Page<Task> findByAssignedToIdAndStatusAndTaskTypeAndGroupId(Long userId, Status status, TaskType taskType, Long groupId, Pageable pageable);

        // Filter by priority
        Page<Task> findByAssignedToIdAndPriorityAndTaskTypeAndGroupId(Long userId, Priority priority, TaskType taskType, Long groupId, Pageable pageable);

        // Filter by category (case-insensitive)
        Page<Task> findByAssignedToIdAndCategoryIgnoreCaseAndTaskTypeAndGroupId(Long userId, String category, TaskType taskType, Long groupId, Pageable pageable);

        // Combined filter (status + priority + category)
        Page<Task> findByAssignedToIdAndStatusAndPriorityAndCategoryIgnoreCaseAndTaskTypeAndGroupId(Long userId, Status status, Priority priority, String category, TaskType taskType, Long groupId, Pageable pageable);

        Page<Task> findByAssignedToIdAndStatusAndPriorityAndTaskTypeAndGroupId(Long userId, Status status, Priority priority, TaskType taskType, Long groupId, Pageable pageable);
        Page<Task> findByAssignedToIdAndStatusAndCategoryIgnoreCaseAndTaskTypeAndGroupId(Long userId, Status status, String category, TaskType taskType, Long groupId, Pageable pageable);
        Page<Task> findByAssignedToIdAndPriorityAndCategoryIgnoreCaseAndTaskTypeAndGroupId(Long userId, Priority priority, String category, TaskType taskType, Long groupId, Pageable pageable);
        Page<Task> findByAssignedToIdAndTaskTypeAndGroupId(Long userId, TaskType taskType, Long groupId, Pageable pageable);

// ---------------- ADMIN SECTION (TaskRepository) ----------------

    // Search by title/description in group
    @Query("SELECT t FROM Task t WHERE t.group.id = :groupId AND t.taskType = :taskType AND " +
            "(LOWER(t.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(t.description) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Task> findByGroupIdAndTitleOrDescription(@Param("groupId") Long groupId,
                                                  @Param("keyword") String keyword,
                                                  @Param("taskType") TaskType taskType,
                                                  Pageable pageable);

    // Search by tags in group
    @Query("SELECT t FROM Task t JOIN t.tags tag WHERE t.group.id = :groupId AND t.taskType = :taskType " +
            "AND LOWER(tag) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Task> findByGroupIdAndTagKeyword(@Param("groupId") Long groupId,
                                          @Param("keyword") String keyword,
                                          @Param("taskType") TaskType taskType,
                                          Pageable pageable);

    // Count by tags in group
    @Query("SELECT COUNT(t) FROM Task t JOIN t.tags tag WHERE t.group.id = :groupId AND t.taskType = :taskType " +
            "AND LOWER(tag) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Long countByGroupIdAndTagKeyword(@Param("groupId") Long groupId,
                                     @Param("keyword") String keyword,
                                     @Param("taskType") TaskType taskType);

    // Count by title/description in group
    @Query("SELECT COUNT(t) FROM Task t WHERE t.group.id = :groupId AND t.taskType = :taskType AND " +
            "(LOWER(t.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(t.description) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Long countByGroupIdAndTitleOrDescription(@Param("groupId") Long groupId,
                                             @Param("keyword") String keyword,
                                             @Param("taskType") TaskType taskType);

    // Filters
    Page<Task> findByGroupIdAndPriorityAndCategoryIgnoreCaseAndTaskType(Long groupId, Priority priority, String category, TaskType taskType, Pageable pageable);
    Page<Task> findByGroupIdAndStatusAndCategoryIgnoreCaseAndTaskType(Long groupId, Status status, String category, TaskType taskType, Pageable pageable);
    Page<Task> findByGroupIdAndStatusAndPriorityAndTaskType(Long groupId, Status status, Priority priority, TaskType taskType, Pageable pageable);
    Page<Task> findByGroupIdAndStatusAndTaskType(Long groupId, Status status, TaskType taskType, Pageable pageable);
    Page<Task> findByGroupIdAndPriorityAndTaskType(Long groupId, Priority priority, TaskType taskType, Pageable pageable);
    Page<Task> findByGroupIdAndCategoryIgnoreCaseAndTaskType(Long groupId, String category, TaskType taskType, Pageable pageable);
    Page<Task> findByGroupIdAndStatusAndPriorityAndCategoryIgnoreCaseAndTaskType(Long groupId, Status status, Priority priority, String category, TaskType taskType, Pageable pageable);
    Page<Task> findByGroupIdAndTaskType(Long groupId, TaskType taskType, Pageable pageable);

    // Recent & Due
    List<Task> findByGroupIdAndLastVisitedAfterAndTaskType(Long groupId, LocalDateTime cutoff, TaskType taskType);
    List<Task> findByGroupIdAndDueDateBetweenAndTaskType(Long groupId, LocalDateTime start, LocalDateTime end, TaskType taskType);




}
