package com.company.kanban.repository;

import com.company.kanban.model.entity.Task;
import com.company.kanban.model.enums.Priority;
import com.company.kanban.model.enums.Status;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Repository
@RepositoryRestResource(exported = false)
public interface TaskRepository extends JpaRepository<Task, Long> {

    @Lock(value = LockModeType.OPTIMISTIC)
    Page<Task> findByStatus(Status status, Pageable pageable);
    Page<Task> findByStatusAndPriority(Status status, Priority priority, Pageable pageable);
    Page<Task> findByPriority(Priority priority, Pageable pageable);
    Page<Task> findAll(Pageable pageable);
}
