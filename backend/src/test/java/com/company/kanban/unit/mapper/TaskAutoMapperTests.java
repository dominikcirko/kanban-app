//package com.company.kanban.unit.mapper;
//
//import com.company.kanban.mapper.TaskAutoMapper;
//import com.company.kanban.model.dto.TaskDTO;
//import com.company.kanban.model.entity.Task;
//import com.company.kanban.model.enums.Priority;
//import com.company.kanban.model.enums.Status;
//import org.junit.jupiter.api.Assertions;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.junit.jupiter.api.Test;
//
//import org.mockito.junit.jupiter.MockitoExtension;
//
//
//
//@ExtendWith(MockitoExtension.class)
//public class TaskAutoMapperTests {
//
//    @Test
//    public void taskAutoMapper_ConvertToDto_ReturnsTaskDTO() {
//
//        Task task = Task.builder()
//                .title("title1")
//                .description("desc1")
//                .status(Status.DONE)
//                .priority(Priority.HIGH)
//                .build();
//
//        TaskDTO taskDTO = TaskAutoMapper.convertToDto(task);
//
//        Assertions.assertNotNull(taskDTO);
//        Assertions.assertEquals(task.getTitle(), taskDTO.getTitle());
//        Assertions.assertEquals(task.getDescription(), taskDTO.getDescription());
//
//    }
//
//    @Test
//    public void taskAutoMapper_ConvertToEntity_ReturnsTask() {
//
//        TaskDTO taskDto = TaskDTO.builder()
//                .title("title1")
//                .description("desc1")
//                .status(Status.DONE)
//                .priority(Priority.HIGH)
//                .build();
//
//        Task task = TaskAutoMapper.convertToEntity(taskDto);
//
//        Assertions.assertNotNull(task);
//        Assertions.assertEquals(taskDto.getTitle(), task.getTitle());
//        Assertions.assertEquals(taskDto.getDescription(), task.getDescription());
//
//    }
//
//
//
//
//}
