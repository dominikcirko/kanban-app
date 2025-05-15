package com.company.kanban.controller;

import com.company.kanban.model.dto.TaskDTO;
import com.company.kanban.model.entity.Task;
import com.company.kanban.model.enums.Priority;
import com.company.kanban.model.enums.Status;
import com.company.kanban.service.interfaces.TaskService;
import com.company.kanban.mapper.TaskAutoMapper;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Optional;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@SecurityRequirement(name = "bearerAuth")
@RequestMapping("/api/tasks")
public class TaskController {

    private TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping
    public ResponseEntity<Page<TaskDTO>> getAllTasks(
            @RequestParam(required = false) Status status,
            @RequestParam(required = false) Priority priority,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(name = "sort", required = false) String sortParam)
    {
        Pageable pageable = taskService.buildPageable(page, size, sortParam);
        Page<TaskDTO> tasksPage = taskService.getTasks(status, priority, pageable);
        return ResponseEntity.ok(tasksPage);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<TaskDTO>> getTaskById(@PathVariable Long id) {
        return taskService.getTaskById(id)
                .map(task -> ResponseEntity.ok(EntityModel.of(task)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<EntityModel<TaskDTO>> createTask(@Valid @RequestBody Task task) {
        TaskDTO created = taskService.createTask(task);
        return ResponseEntity
                .created(linkTo(methodOn(TaskController.class).getTaskById(created.getId())).toUri())
                .body(EntityModel.of(created));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<TaskDTO>> updateTask(@PathVariable Long id,
                                                           @Valid @RequestBody Task task) {
        TaskDTO updated = taskService.updateTask(task);
        return ResponseEntity.ok(EntityModel.of(updated));
    }

    @PatchMapping(value = "/{id}", consumes = "application/mergepatch+json")
    public ResponseEntity<EntityModel<TaskDTO>> partialUpdateTask(@PathVariable Long id,
                                                                  @RequestBody String patchJson) throws IOException {
        Optional<TaskDTO> existing = taskService.getTaskById(id);
        if (existing.isEmpty())
            return ResponseEntity.notFound().build();

        Task task = TaskAutoMapper.convertToEntity(existing.get());
        TaskDTO updated = taskService.partialUpdateTask(task, patchJson);

        return ResponseEntity.ok(EntityModel.of(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }

}
