package com.company.kanban.utils;

import com.company.kanban.model.entity.Task;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
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
