package com.company.kanban.unit.mapper;


import com.company.kanban.mapper.JsonMergePatch;
import com.company.kanban.model.dto.TaskDTO;
import com.company.kanban.model.entity.Task;
import com.company.kanban.model.enums.Priority;
import com.company.kanban.model.enums.Status;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class JsonMergePatchTests {

    private ObjectMapper objectMapper;
    private JsonMergePatch jsonMergePatch;

    @BeforeEach
    public void setup() {
        objectMapper = mock(ObjectMapper.class);
        ObjectReader objectReader = mock(ObjectReader.class);
        when(objectMapper.readerForUpdating(any(Task.class))).thenReturn(objectReader);
        jsonMergePatch = new JsonMergePatch(objectMapper);
    }

    @Test
    @SneakyThrows
    public void jsonMergePatch_MergePatchTask_ReturnsTask() {
        Task existingTask = Task.builder()
                .title("Old Title")
                .description("Old Description")
                .status(Status.TO_DO)
                .priority(Priority.LOW)
                .build();

        String patchJson = "{\"title\": \"Updated Title\", \"description\": \"Updated Description\"}";

        Task updatedTask = Task.builder()
                .title("Updated Title")
                .description("Updated Description")
                .status(Status.TO_DO)
                .priority(Priority.LOW)
                .build();

        ObjectReader objectReader = objectMapper.readerForUpdating(existingTask);
        when(objectReader.readValue(patchJson, Task.class)).thenReturn(updatedTask);

        Task result = jsonMergePatch.mergePatchTask(existingTask, patchJson);

        assertNotNull(result);
        assertEquals("Updated Title", result.getTitle());
        assertEquals("Updated Description", result.getDescription());

        verify(objectMapper, times(2)).readerForUpdating(existingTask);
        verify(objectReader).readValue(patchJson, Task.class);
    }
}
