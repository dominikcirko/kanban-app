package com.company.kanban.service.interfaces;

import com.company.kanban.model.dto.TaskDTO;
import com.company.kanban.model.entity.Task;
import com.company.kanban.model.enums.Priority;
import com.company.kanban.model.enums.Status;
import jakarta.persistence.OptimisticLockException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.IOException;
import java.util.Optional;

public interface TaskService {

    Page<TaskDTO> getTasks(Status status, Priority priority, Pageable pageable);

    Optional<TaskDTO> getTaskById(Long id);

    TaskDTO createTask(Task task);

    TaskDTO updateTask(Task task) throws OptimisticLockException, OptimisticLockingFailureException;

    void deleteTask(Long id) throws OptimisticLockException, OptimisticLockingFailureException;

    TaskDTO partialUpdateTask(Task task, String jsonPartialUpdate) throws IOException, OptimisticLockException, OptimisticLockingFailureException;

    Pageable buildPageable(int page, int size, String sortParam);
}
