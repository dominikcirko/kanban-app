package com.company.kanban.service.implementations;

import ch.qos.logback.classic.Logger;
import com.company.kanban.controller.TaskWebSocketController;
import com.company.kanban.model.dto.TaskDTO;
import com.company.kanban.model.entity.Task;
import com.company.kanban.model.enums.Priority;
import com.company.kanban.model.enums.Status;
import com.company.kanban.repository.TaskRepository;
import com.company.kanban.mapper.JsonMergePatch;
import com.company.kanban.mapper.TaskDtoAssembler;
import com.company.kanban.service.interfaces.TaskService;
import jakarta.persistence.OptimisticLockException;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final TaskDtoAssembler taskDtoAssembler;
    private final JsonMergePatch jsonMergePatch;
    private final TaskWebSocketController webSocketController;

    private static final Logger log = (Logger) LoggerFactory.getLogger(TaskServiceImpl.class);
    private final AtomicLong cacheHits = new AtomicLong(0);
    private final AtomicLong cacheMisses = new AtomicLong(0);

    public TaskServiceImpl(TaskRepository taskRepository,
                           JsonMergePatch jsonMergePatch,
                           TaskDtoAssembler taskDtoAssembler,
                           TaskWebSocketController webSocketController) {
        this.taskRepository = taskRepository;
        this.jsonMergePatch = jsonMergePatch;
        this.taskDtoAssembler = taskDtoAssembler;
        this.webSocketController = webSocketController;
    }

    @Override
    @Transactional(readOnly = true)
    @CacheEvict(value = "tasks", allEntries = true)
    @Cacheable(value = "tasks", key = "{#status, #priority, #pageable.pageNumber, #pageable.pageSize}")
    public Page<TaskDTO> getTasks(Status status, Priority priority, Pageable pageable) {
        Page<Task> taskEntitiesPage;
        long startTime = System.currentTimeMillis();

        long missCount = cacheMisses.incrementAndGet();

        if (status != null && priority != null) {
            taskEntitiesPage = taskRepository.findByStatusAndPriority(status, priority, pageable);
        } else if (status != null) {
            taskEntitiesPage = taskRepository.findByStatus(status, pageable);
        } else if (priority != null) {
            taskEntitiesPage = taskRepository.findByPriority(priority, pageable);
        } else {
            taskEntitiesPage = taskRepository.findAll(pageable);
        }

        log.info("Cache miss - Fetching tasks from database for status={}, priority={}", status, priority);
        long endTime = System.currentTimeMillis();
        log.info("DATABASE ACCESS: Fetched tasks in {}ms (Total cache misses: {}, params: status={}, priority={})",
                (endTime - startTime), missCount, status, priority);

        return taskEntitiesPage.map(taskDtoAssembler::toModel);
    }

    @Override
    public Optional<TaskDTO> getTaskById(Long id) {
        return taskRepository.findById(id)
                .map(taskDtoAssembler::toModel);
    }

    @Override
    public TaskDTO createTask(Task task) {
        Task savedTask = taskRepository.save(task);
        TaskDTO taskDTO = taskDtoAssembler.toModel(savedTask);
        webSocketController.notifyTaskCreated(taskDTO);
        return taskDTO;
    }

    @Override
    public TaskDTO updateTask(Task task) {
        try {
            Task savedTask = taskRepository.save(task);
            TaskDTO taskDTO = taskDtoAssembler.toModel(savedTask);
            webSocketController.notifyTaskUpdated(taskDTO);
            return taskDTO;
        } catch (OptimisticLockingFailureException | OptimisticLockException e) {
            throw new OptimisticLockException("Task was updated by another user. Please reload and try again.");
        }
    }

    @Override
    public TaskDTO partialUpdateTask(Task task, String jsonPartialUpdate) {
        try {
            Task updatedTask = jsonMergePatch.mergePatchTask(task, jsonPartialUpdate);
            Task savedTask = taskRepository.save(updatedTask);
            TaskDTO taskDTO = taskDtoAssembler.toModel(savedTask);
            webSocketController.notifyTaskUpdated(taskDTO);
            return taskDTO;
        } catch (OptimisticLockingFailureException | OptimisticLockException e) {
            throw new OptimisticLockException("Task was updated during your edit. Please reload and try again.");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteTask(Long id) {
        try {
            taskRepository.deleteById(id);
            webSocketController.notifyTaskDeleted(id);
        } catch (OptimisticLockingFailureException | OptimisticLockException e) {
            throw new OptimisticLockException("Task was modified before deletion. Please refresh and try again.");
        }
    }

    @Override
    public Pageable buildPageable(int page, int size, String sortParam){

        if (sortParam != null && !sortParam.isEmpty()) {
            String[] sortCriteria = sortParam.split(";");
            List<Sort.Order> orders = new ArrayList<>();

            for (String criterion : sortCriteria) {
                if (criterion.contains(",")) {
                    String[] parts = criterion.split(",");
                    String property = parts[0].trim();
                    if (parts.length > 1 && parts[1].trim().equalsIgnoreCase("desc")) {
                        orders.add(Sort.Order.desc(property));
                    } else
                        orders.add(Sort.Order.asc(property));
                } else
                    orders.add(Sort.Order.asc(criterion.trim()));
            }
            return PageRequest.of(page, size, Sort.by(orders));
        }
        else
            return PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "id"));

    }



}
