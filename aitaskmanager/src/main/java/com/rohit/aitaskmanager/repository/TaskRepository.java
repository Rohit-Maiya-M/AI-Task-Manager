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

    // Due Date
    List<Task> findByCreatedByIdAndDueDateBetweenAndTaskType(Long userId, LocalDateTime start, LocalDateTime end, TaskType taskType);

    // Common
    Task findByIdAndCreatedByIdAndTaskType(Long taskId, Long userId, TaskType taskType);
    Page<Task> findByCreatedByIdAndTaskType(Long userId, TaskType taskType, Pageable pageable);

}
