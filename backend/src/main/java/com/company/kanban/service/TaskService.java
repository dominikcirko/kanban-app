package com.company.kanban.service;

import com.company.kanban.model.dto.TaskDTO;
import com.company.kanban.model.entity.Task;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface TaskService {

    List<TaskDTO> getAllTasks();

    Optional<TaskDTO> getTaskById(Long id);

    TaskDTO createTask(Task task);

    TaskDTO updateTask(Task task);

    void deleteTask(Long id);

    TaskDTO partialUpdateTask(Task task, String jsonPartialUpdate) throws IOException;
}
