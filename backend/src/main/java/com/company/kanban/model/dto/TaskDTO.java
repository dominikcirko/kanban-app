package com.company.kanban.model.dto;

import com.company.kanban.model.enums.Priority;
import com.company.kanban.model.enums.Status;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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

    @NotBlank(message = "Title cannot be empty")
    @Size(min = 3, max = 100, message = "Title must be between 3 and 100 characters")
    private String title;

    @NotBlank(message = "Description cannot be empty")
    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String description;

    @NotNull(message = "Status is required")
    private Status status;

    @NotNull(message = "Priority is required")
    private Priority priority;

    private Long version;
}