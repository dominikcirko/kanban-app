package com.company.kanban.mapper;

import com.company.kanban.model.dto.TaskDTO;
import com.company.kanban.model.entity.Task;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

public class TaskAutoMapper {

    private TaskAutoMapper() {}

    private static final ObjectMapper objectMapper = createCustomObjectMapper();

    private static ObjectMapper createCustomObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();

        //ignore unknown properties (hateoas links) during deserialization
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return mapper;
    }
    public static TaskDTO convertToDto(Task task) {
        return objectMapper.convertValue(task, TaskDTO.class);
    }

    public static Task convertToEntity(TaskDTO taskDto) {
        return objectMapper.convertValue(taskDto, Task.class);
    }
}
