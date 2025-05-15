package com.company.kanban.controller;

import com.company.kanban.model.dto.TaskDTO;
import com.company.kanban.model.entity.Task;
import com.company.kanban.model.enums.Priority;
import com.company.kanban.model.enums.Status;
import com.company.kanban.service.interfaces.TaskService;
import com.company.kanban.mapper.TaskAutoMapper;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.Optional;

@Controller
@AllArgsConstructor
public class TaskGraphQLController {

    private final TaskService taskService;

    @QueryMapping
    public List<TaskDTO> tasks(@Argument Status status,
                               @Argument Priority priority,
                               @Argument Integer page,
                               @Argument Integer size) {
        Pageable pageable = taskService.buildPageable(page != null ? page : 0,
                size != null ? size : 10,
                null);
        Page<TaskDTO> tasksPage = taskService.getTasks(status, priority, pageable);
        return tasksPage.getContent();
    }

    @QueryMapping
    public Optional<TaskDTO> task(@Argument Long id) {
        return taskService.getTaskById(id);
    }

    @MutationMapping
    public TaskDTO createTask(@Argument String title,
                              @Argument String description,
                              @Argument Status status,
                              @Argument Priority priority) {
        Task task = new Task();
        task.setTitle(title);
        task.setDescription(description);
        task.setStatus(status);
        task.setPriority(priority);

        return taskService.createTask(task);
    }

    @MutationMapping
    public TaskDTO updateTask(@Argument Long id,
                              @Argument String title,
                              @Argument String description,
                              @Argument Status status,
                              @Argument Priority priority) {
        Optional<TaskDTO> existingTaskOpt = taskService.getTaskById(id);
        if (existingTaskOpt.isEmpty()) {
            return null;
        }

        Task task = TaskAutoMapper.convertToEntity(existingTaskOpt.get());

        if (title != null) task.setTitle(title);
        if (description != null) task.setDescription(description);
        if (status != null) task.setStatus(status);
        if (priority != null) task.setPriority(priority);

        return taskService.updateTask(task);
    }

    @MutationMapping
    public boolean deleteTask(@Argument Long id) {
        try {
            taskService.deleteTask(id);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}