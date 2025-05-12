package com.company.kanban.service;

import com.company.kanban.mapper.JsonMergePatch;
import com.company.kanban.mapper.TaskDtoAssembler;
import com.company.kanban.model.dto.TaskDTO;
import com.company.kanban.model.entity.Task;
import com.company.kanban.model.enums.Priority;
import com.company.kanban.model.enums.Status;
import com.company.kanban.repository.TaskRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.io.IOException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TaskServiceTests {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private JsonMergePatch jsonMergePatch;

    @Mock
    private TaskDtoAssembler taskDtoAssembler;

    @InjectMocks
    private TaskServiceImpl taskService;

    @Test
    public void taskService_CreateTask_ReturnsTaskDto() {
        //arrange
        Task task = Task.builder()
                .title("title1")
                .description("desc1")
                .status(Status.DONE)
                .priority(Priority.HIGH)
                .build();

        //act
        when(taskRepository.save(Mockito.any(Task.class)))
                .thenReturn(task);

        when(taskDtoAssembler.toModel(task))
                .thenReturn(
                        TaskDTO.builder()
                                .title(task.getTitle())
                                .description(task.getDescription())
                                .status(task.getStatus())
                                .priority(task.getPriority())
                                .build()
                );

        TaskDTO savedTask = taskService.createTask(task);

        //assert
        Assertions.assertThat(savedTask).isNotNull();
    }

    @Test
    public void taskService_UpdateTask_ReturnsTaskDto() {

        //arrange
        Task task = Task.builder()
                .title("title1")
                .description("desc1")
                .status(Status.DONE)
                .priority(Priority.HIGH)
                .build();

        //act
        when(taskRepository.save(Mockito.any(Task.class)))
                .thenReturn(task);

        when(taskDtoAssembler.toModel(Mockito.any(Task.class)))
                .thenAnswer(invocation -> {
                    Task t = invocation.getArgument(0);
                    return TaskDTO.builder()
                            .title(t.getTitle())
                            .description(t.getDescription())
                            .status(t.getStatus())
                            .priority(t.getPriority())
                            .build();
                });

        task.setTitle("title2");
        task.setDescription("desc2");
        task.setStatus(Status.TO_DO);
        task.setPriority(Priority.LOW);

        TaskDTO updatedTask = taskService.updateTask(task);

        //assert
        Assertions.assertThat(updatedTask).isNotNull();
        Assertions.assertThat(updatedTask.getTitle()).isEqualTo(task.getTitle());
        Assertions.assertThat(updatedTask.getDescription()).isEqualTo(task.getDescription());
        Assertions.assertThat(updatedTask.getStatus()).isEqualTo(task.getStatus());
        Assertions.assertThat(updatedTask.getPriority()).isEqualTo(task.getPriority());

    }

    @Test
    public void taskService_DeleteTask_ReturnsVoid() {
        Long taskId = 1L;
        taskService.deleteTask(taskId);

        verify(taskRepository).deleteById(taskId);
    }

    @Test
    public void TaskService_PartialUpdateTask_ReturnsTaskDTO() throws IOException {
        //arrange
        Task existingTask = new Task();
        existingTask.setTitle("Original Task");

        TaskDTO expectedTaskDTO = new TaskDTO();
        expectedTaskDTO.setTitle("Updated Task");

        String jsonPartialUpdate = "{\"title\":\"Updated Task\"}";

        //act
        Task updatedTask = new Task();
        updatedTask.setTitle("Updated Task");

        when(jsonMergePatch.mergePatchTask(existingTask, jsonPartialUpdate))
                .thenReturn(updatedTask);
        when(taskRepository.save(updatedTask))
                .thenReturn(updatedTask);
        when(taskDtoAssembler.toModel(updatedTask))
                .thenReturn(expectedTaskDTO);

        TaskDTO result = taskService.partialUpdateTask(existingTask, jsonPartialUpdate);

        //assert
        assertNotNull(result);
        assertEquals(expectedTaskDTO, result);

        verify(jsonMergePatch).mergePatchTask(existingTask, jsonPartialUpdate);
        verify(taskRepository).save(updatedTask);
        verify(taskDtoAssembler).toModel(updatedTask);
    }

    @Test
    void taskService_BuildPageable_DefaultSorting_ReturnsPageable() {
        // arrange
        int page = 0;
        int size = 10;
        String sortParam = null;

        // act
        Pageable result = taskService.buildPageable(page, size, sortParam);

        // assert
        assertEquals(page, result.getPageNumber());
        assertEquals(size, result.getPageSize());
        assertEquals(Sort.by(Sort.Direction.ASC, "id"), result.getSort());
    }

    @Test
    void taskService_BuildPageable_SingleAscendingSortParam_ReturnsPageable() {
        // arrange
        int page = 1;
        int size = 20;
        String sortParam = "title";

        // act
        Pageable result = taskService.buildPageable(page, size, sortParam);

        // assert
        assertEquals(page, result.getPageNumber());
        assertEquals(size, result.getPageSize());
        assertTrue(Objects.requireNonNull(result.getSort().getOrderFor("title")).isAscending());
    }

    @Test
    void taskService_BuildPageable_SingleDescendingSortParam_ReturnsPageable() {
        // arrange
        int page = 1;
        int size = 20;
        String sortParam = "title,desc";

        // act
        Pageable result = taskService.buildPageable(page, size, sortParam);

        // assert
        assertEquals(page, result.getPageNumber());
        assertEquals(size, result.getPageSize());
        assertTrue(Objects.requireNonNull(result.getSort().getOrderFor("title")).isDescending());
    }

    @Test
    void taskService_BuildPageable_MultipleSortParams_ReturnsPageable() {
        // arrange
        int page = 2;
        int size = 15;
        String sortParam = "title,desc;status,asc;priority,desc";

        // act
        Pageable result = taskService.buildPageable(page, size, sortParam);

        // assert
        assertEquals(page, result.getPageNumber());
        assertEquals(size, result.getPageSize());

        Sort sort = result.getSort();
        assertNotNull(sort);

        Iterator<Sort.Order> orderIterator = sort.iterator();
        assertTrue(orderIterator.hasNext());

        Sort.Order firstOrder = orderIterator.next();
        assertEquals("title", firstOrder.getProperty());
        assertTrue(firstOrder.isDescending());

        Sort.Order secondOrder = orderIterator.next();
        assertEquals("status", secondOrder.getProperty());
        assertTrue(secondOrder.isAscending());

        Sort.Order thirdOrder = orderIterator.next();
        assertEquals("priority", thirdOrder.getProperty());
        assertTrue(thirdOrder.isDescending());

        assertFalse(orderIterator.hasNext());
    }

    @Test
    void taskService_BuildPageable_EmptySortParam_ReturnsPageable() {
        // arrange
        int page = 0;
        int size = 10;
        String sortParam = "";

        // act
        Pageable result = taskService.buildPageable(page, size, sortParam);

        // assert
        assertEquals(page, result.getPageNumber());
        assertEquals(size, result.getPageSize());
        assertEquals(Sort.by(Sort.Direction.ASC, "id"), result.getSort());
    }


}
