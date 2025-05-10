package com.company.kanban.service;

import com.company.kanban.model.dto.TaskDTO;
import com.company.kanban.model.entity.Task;
import com.company.kanban.repository.TaskRepository;
import com.company.kanban.utils.JsonMergePatch;
import com.company.kanban.utils.TaskDtoAssembler;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final TaskDtoAssembler taskDtoAssembler;
    private final JsonMergePatch jsonMergePatch;

    public TaskServiceImpl(TaskRepository taskRepository, JsonMergePatch jsonMergePatch, TaskDtoAssembler taskDtoAssembler) {
        this.taskRepository = taskRepository;
        this.jsonMergePatch = jsonMergePatch;
        this.taskDtoAssembler = taskDtoAssembler;
    }

    @Override
    public List<TaskDTO> getAllTasks() {
        List<Task> tasks = taskRepository.findAll();
        return tasks.stream()
                .map(taskDtoAssembler::toModel)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<TaskDTO> getTaskById(Long id) {
        return taskRepository.findById(id)
                .map(taskDtoAssembler::toModel);
    }

    @Override
    public TaskDTO createTask(Task task) {
        Task savedTask = taskRepository.save(task);
        return taskDtoAssembler.toModel(savedTask);
    }

    @Override
    public TaskDTO updateTask(Task task) {
        Task savedTask = taskRepository.save(task);
        return taskDtoAssembler.toModel(savedTask);
    }

    @Override
    public TaskDTO partialUpdateTask(Task task, String jsonPartialUpdate) throws IOException {
        Task updatedTask = jsonMergePatch.mergePatchTask(task, jsonPartialUpdate);
        Task savedTask = taskRepository.save(updatedTask);
        return taskDtoAssembler.toModel(savedTask);
    }

    @Override
    public void deleteTask(Long id) {
        taskRepository.deleteById(id);
    }




}
