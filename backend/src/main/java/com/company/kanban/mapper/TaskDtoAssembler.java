package com.company.kanban.mapper;

import com.company.kanban.controller.TaskController;
import com.company.kanban.model.dto.TaskDTO;
import com.company.kanban.model.entity.Task;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class TaskDtoAssembler extends RepresentationModelAssemblerSupport<Task, TaskDTO> {

    public TaskDtoAssembler() {
        super(TaskController.class, TaskDTO.class);
    }

    //use TaskAutoMapper to convert to TaskDTO, then add hateoas links to payload
    @Override
    public TaskDTO toModel(Task entity) {
        TaskDTO taskDTO = TaskAutoMapper.convertToDto(entity);
        taskDTO.add(linkTo(methodOn(TaskController.class).getTaskById(entity.getId())).withSelfRel());
        return taskDTO;
    }
}