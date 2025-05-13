package com.company.kanban.controller;

import com.company.kanban.model.dto.TaskDTO;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Controller
public class TaskWebSocketController {

    private final SimpMessagingTemplate messagingTemplate;

    public TaskWebSocketController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }


    public void notifyTaskCreated(TaskDTO task) {
        messagingTemplate.convertAndSend("/topic/tasks",
                createNotification("CREATE", task));
    }


    public void notifyTaskUpdated(TaskDTO task) {
        messagingTemplate.convertAndSend("/topic/tasks",
                createNotification("UPDATE", task));
    }


    public void notifyTaskDeleted(Long taskId) {
        Map<String, Object> notification = new HashMap<>();
        notification.put("type", "DELETE");
        notification.put("taskId", taskId);
        notification.put("timestamp", new Date());

        messagingTemplate.convertAndSend("/topic/tasks", notification);
    }

    private Map<String, Object> createNotification(String type, TaskDTO task) {
        Map<String, Object> notification = new HashMap<>();
        notification.put("type", type);
        notification.put("task", task);
        notification.put("timestamp", new Date());
        return notification;
    }
}
