package com.company.kanban.utils;

import com.company.kanban.controller.TaskController;
import com.company.kanban.model.dto.TaskDTO;
import com.company.kanban.model.entity.Task;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.StreamSupport;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

//responsible for adding links to converted (Entity to DTO) objects
@Component
public class TaskDtoAssembler extends RepresentationModelAssemblerSupport<Task, TaskDTO> {

    public TaskDtoAssembler() {
        super(TaskController.class, TaskDTO.class);
    }
    //add api links to converted (Entity to DTO) objects
    @Override
    public TaskDTO toModel(Task entity) {
        TaskDTO taskDTO = AutoMapper.convertToDto(entity);
        taskDTO.add(linkTo(methodOn(TaskController.class).getTaskById(entity.getId())).withSelfRel());
        return taskDTO;
    }

    //take object from toModel(), put in a list and create global api link to itself
    @Override
    public CollectionModel<TaskDTO> toCollectionModel(Iterable<? extends Task> entities) {
        List<TaskDTO> dtoList = StreamSupport.stream(entities.spliterator(), false)
                .map(this::toModel)
                .toList();

        return CollectionModel.of(dtoList,
                linkTo(methodOn(TaskController.class).getAllTasks()).withSelfRel());
    }
}