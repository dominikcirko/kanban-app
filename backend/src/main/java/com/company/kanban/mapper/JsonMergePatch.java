package com.company.kanban.mapper;

import com.company.kanban.model.entity.Task;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class JsonMergePatch {

    private final ObjectMapper objectMapper;

    public JsonMergePatch(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public Task mergePatchTask(Task existingTask, String patchJson) throws IOException {
        return objectMapper.readerForUpdating(existingTask)
                .readValue(patchJson, Task.class);
    }
}
