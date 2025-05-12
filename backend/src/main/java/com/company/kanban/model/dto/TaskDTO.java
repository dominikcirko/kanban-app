package com.company.kanban.model.dto;

import com.company.kanban.model.enums.Priority;
import com.company.kanban.model.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TaskDTO extends RepresentationModel<TaskDTO> {
    private Long id;
    private String title;
    private String description;
    private Status status;
    private Priority priority;
}