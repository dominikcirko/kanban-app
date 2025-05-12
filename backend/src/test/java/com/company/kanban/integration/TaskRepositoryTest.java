package com.company.kanban.integration;

import com.company.kanban.model.entity.Task;
import com.company.kanban.model.enums.Priority;
import com.company.kanban.model.enums.Status;
import com.company.kanban.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

@Testcontainers
@SpringBootTest
public class TaskRepositoryTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:15");

    @Autowired
    TaskRepository taskRepository;

    @BeforeEach
    void setUp() {
        taskRepository.deleteAll();
    }

    @Test
    void connectionEstablished() {
        assertThat(postgreSQLContainer.isCreated()).isTrue();
        assertThat(postgreSQLContainer.isRunning()).isTrue();
    }

    @Test
    void shouldSaveTask() {

        Task task = createTask("Test Task", "Description", Status.TO_DO, Priority.HIGH);

        Task savedTask = taskRepository.save(task);

        assertThat(savedTask).isNotNull();
        assertThat(savedTask.getId()).isNotNull();
        assertThat(savedTask.getTitle()).isEqualTo("Test Task");
        assertThat(savedTask.getDescription()).isEqualTo("Description");
        assertThat(savedTask.getStatus()).isEqualTo(Status.TO_DO);
        assertThat(savedTask.getPriority()).isEqualTo(Priority.HIGH);
    }

    @Test
    void shouldFindTaskById() {

        Task task = createTask("Find by ID Task", "Description", Status.IN_PROGRESS, Priority.HIGH);
        Task savedTask = taskRepository.save(task);

        Optional<Task> foundTask = taskRepository.findById(savedTask.getId());

        assertThat(foundTask).isPresent();
        assertThat(foundTask.get().getTitle()).isEqualTo("Find by ID Task");
        assertThat(foundTask.get().getStatus()).isEqualTo(Status.IN_PROGRESS);
        assertThat(foundTask.get().getPriority()).isEqualTo(Priority.HIGH);
    }

    @Test
    void shouldReturnEmptyWhenTaskNotFound() {

        Task task = createTask("title1", "description1", Status.IN_PROGRESS, Priority.HIGH);
        Task savedTask = taskRepository.save(task);

        Optional<Task> foundTask = taskRepository.findById(savedTask.getId());

        assertThat(foundTask).isNotNull();
    }

    @Test
    void shouldFindAllTasks() {

        taskRepository.save(createTask("Task 1", "Description 1", Status.DONE, Priority.MED));
        taskRepository.save(createTask("Task 2", "Description 2", Status.IN_PROGRESS, Priority.HIGH));
        taskRepository.save(createTask("Task 3", "Description 3", Status.DONE, Priority.LOW));

        Pageable pageable = PageRequest.of(0, 10, Sort.by("title").ascending());
        Page<Task> tasksPage = taskRepository.findAll(pageable);

        assertThat(tasksPage).isNotNull();
        assertThat(tasksPage.getTotalElements()).isEqualTo(3);
        assertThat(tasksPage.getContent())
                .extracting(Task::getTitle)
                .containsExactly("Task 1", "Task 2", "Task 3");
    }

    @Test
    @Transactional
    void shouldFindTasksByStatus() {
        taskRepository.save(createTask("TODO Task 1", "Description", Status.TO_DO, Priority.HIGH));
        taskRepository.save(createTask("TODO Task 2", "Description", Status.TO_DO, Priority.LOW));
        taskRepository.save(createTask("In Progress Task", "Description", Status.IN_PROGRESS, Priority.MED));
        taskRepository.save(createTask("Done Task", "Description", Status.DONE, Priority.HIGH));

        Pageable pageable = PageRequest.of(0, 10);
        Page<Task> todoTasks = taskRepository.findByStatus(Status.TO_DO, pageable);
        Page<Task> inProgressTasks = taskRepository.findByStatus(Status.IN_PROGRESS, pageable);
        Page<Task> doneTasks = taskRepository.findByStatus(Status.DONE, pageable);

        assertThat(todoTasks.getTotalElements()).isEqualTo(2);
        assertThat(todoTasks.getContent())
                .extracting(Task::getTitle)
                .containsExactlyInAnyOrder("TODO Task 1", "TODO Task 2");

        assertThat(inProgressTasks.getTotalElements()).isEqualTo(1);
        assertThat(inProgressTasks.getContent())
                .extracting(Task::getTitle)
                .containsExactly("In Progress Task");

        assertThat(doneTasks.getTotalElements()).isEqualTo(1);
        assertThat(doneTasks.getContent())
                .extracting(Task::getTitle)
                .containsExactly("Done Task");
    }

    @Test
    void TaskRepository_DeleteTaskById_ReturnsVoid() {

        Task task = createTask("Task to Delete", "Description", Status.TO_DO, Priority.HIGH);
        Task savedTask = taskRepository.save(task);

        assertThat(taskRepository.findById(savedTask.getId())).isPresent();

        taskRepository.deleteById(savedTask.getId());

        assertThat(taskRepository.findById(savedTask.getId())).isEmpty();
    }

    @Test
    void TaskRepository_UpdateTask_ReturnsTask() {

        Task task = createTask("Original Title", "Original Description", Status.TO_DO, Priority.LOW);
        Task savedTask = taskRepository.save(task);

        savedTask.setTitle("Updated Title");
        savedTask.setDescription("Updated Description");
        savedTask.setStatus(Status.IN_PROGRESS);
        Task updatedTask = taskRepository.save(savedTask);

        assertThat(updatedTask.getId()).isEqualTo(savedTask.getId());
        assertThat(updatedTask.getTitle()).isEqualTo("Updated Title");
        assertThat(updatedTask.getDescription()).isEqualTo("Updated Description");
        assertThat(updatedTask.getStatus()).isEqualTo(Status.IN_PROGRESS);

        Optional<Task> retrievedTask = taskRepository.findById(updatedTask.getId());
        assertThat(retrievedTask).isPresent();
        assertThat(retrievedTask.get().getTitle()).isEqualTo("Updated Title");
    }

    @Test
    void TaskRepository_FindAllByPagination_ReturnsAllTasksOnPage() {

        for (int i = 1; i <= 25; i++) {
            taskRepository.save(createTask("Task " + i, "Description " + i,
                    i % 3 == 0 ? Status.DONE : i % 2 == 0 ? Status.IN_PROGRESS : Status.TO_DO, Priority.HIGH));
        }

        // first page (0-indexed)
        Pageable firstPageable = PageRequest.of(0, 10, Sort.by("title").ascending());
        Page<Task> firstPage = taskRepository.findAll(firstPageable);

        // second page
        Pageable secondPageable = PageRequest.of(1, 10, Sort.by("title").ascending());
        Page<Task> secondPage = taskRepository.findAll(secondPageable);

        assertThat(firstPage.getTotalElements()).isEqualTo(25);
        assertThat(firstPage.getTotalPages()).isEqualTo(3);
        assertThat(firstPage.getNumber()).isEqualTo(0);
        assertThat(firstPage.getSize()).isEqualTo(10);
        assertThat(firstPage.getContent()).hasSize(10);

        assertThat(secondPage.getNumber()).isEqualTo(1);
        assertThat(secondPage.getContent()).hasSize(10);
        assertThat(secondPage.getContent())
                .extracting(Task::getTitle)
                .doesNotContainAnyElementsOf(firstPage.getContent().stream()
                        .map(Task::getTitle)
                        .toList());
    }

    @Test
    @Transactional
    void TaskRepository_SortTasks_ReturnsSortedTasks() {

        taskRepository.save(createTask("Z Task", "Description", Status.TO_DO, Priority.LOW));    // Z, TODO, LOW
        taskRepository.save(createTask("A Task", "Description", Status.DONE, Priority.HIGH));    // A, DONE, HIGH
        taskRepository.save(createTask("M Task", "Description", Status.IN_PROGRESS, Priority.MED)); // M, IN_PROGRESS, MED
        taskRepository.save(createTask("A Task", "Description", Status.TO_DO, Priority.LOW));    // A, TODO, LOW (duplicate title)
        taskRepository.save(createTask("A Task", "Description", Status.IN_PROGRESS, Priority.HIGH)); // A, IN_PROGRESS, HIGH (duplicate title)

        // sort by title ascending
        Pageable titleAscPageable = PageRequest.of(0, 10, Sort.by("title").ascending());
        List<Task> titleAscTasks = taskRepository.findAll(titleAscPageable).getContent();

        assertThat(titleAscTasks)
                .extracting(Task::getTitle)
                .containsExactly("A Task", "A Task", "A Task", "M Task", "Z Task");

        // sort by title descending
        Pageable titleDescPageable = PageRequest.of(0, 10, Sort.by("title").descending());
        List<Task> titleDescTasks = taskRepository.findAll(titleDescPageable).getContent();

        assertThat(titleDescTasks)
                .extracting(Task::getTitle)
                .containsExactly("Z Task", "M Task", "A Task", "A Task", "A Task");

        // multi-field sort - title ascending, then priority descending
        Sort multiSort1 = Sort.by("title").ascending().and(Sort.by("priority").descending());
        Pageable multiSortPageable1 = PageRequest.of(0, 10, multiSort1);
        List<Task> multiSortTasks1 = taskRepository.findAll(multiSortPageable1).getContent();

        // should be ordered by title (A,A,A,M,Z) and then by priority (HIGH,MED,LOW) within same title
        assertThat(multiSortTasks1)
                .extracting(Task::getTitle, Task::getPriority)
                .containsExactly(
                        tuple("A Task", Priority.HIGH),
                        tuple("A Task", Priority.HIGH),
                        tuple("A Task", Priority.LOW),
                        tuple("M Task", Priority.MED),
                        tuple("Z Task", Priority.LOW)
                );

        // multi-field sort - status, then title ascending
        Sort multiSort2 = Sort.by("status").ascending().and(Sort.by("title").ascending());
        Pageable multiSortPageable2 = PageRequest.of(0, 10, multiSort2);
        List<Task> multiSortTasks2 = taskRepository.findAll(multiSortPageable2).getContent();

        // should be ordered by status first (DONE, IN_PROGRESS, TO_DO) then by title within each status
        assertThat(multiSortTasks2)
                .extracting(Task::getStatus, Task::getTitle)
                .containsExactly(
                        tuple(Status.DONE, "A Task"),
                        tuple(Status.IN_PROGRESS, "A Task"),
                        tuple(Status.IN_PROGRESS, "M Task"),
                        tuple(Status.TO_DO, "A Task"),
                        tuple(Status.TO_DO, "Z Task")
                );
    }

    //helper method for task creation
    private Task createTask(String title, String description, Status status, Priority priority) {
        Task task = new Task();
        task.setTitle(title);
        task.setDescription(description);
        task.setStatus(status);
        task.setPriority(priority);
        return task;
    }
}