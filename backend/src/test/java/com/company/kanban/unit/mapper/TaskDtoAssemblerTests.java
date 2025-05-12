package com.company.kanban.unit.mapper;


import com.company.kanban.mapper.TaskDtoAssembler;
import com.company.kanban.model.dto.TaskDTO;
import com.company.kanban.model.entity.Task;
import com.company.kanban.model.enums.Priority;
import com.company.kanban.model.enums.Status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class TaskDtoAssemblerTests {

    private TaskDtoAssembler assembler;

    @BeforeEach
     void setUp() {
        assembler = new TaskDtoAssembler();
    }

    @Test
    void toModel_ReturnsCorrectDtoWithSelfLink() {
        Task task = Task.builder()
                .id(1L)
                .title("Test Task")
                .description("Test Description")
                .status(Status.TO_DO)
                .priority(Priority.MED)
                .build();

        TaskDTO dto = assembler.toModel(task);

        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(task.getId());
        assertThat(dto.getTitle()).isEqualTo(task.getTitle());
        assertThat(dto.getDescription()).isEqualTo(task.getDescription());
        assertThat(dto.getStatus()).isEqualTo(task.getStatus());
        assertThat(dto.getPriority()).isEqualTo(task.getPriority());

        assertThat(dto.getLinks()).isNotEmpty();
        assertThat(dto.getLink("self")).isPresent();
        assertThat(dto.getLink("self").get().getHref()).contains("/tasks/" + task.getId());
    }
}
