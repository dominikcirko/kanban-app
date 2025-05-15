package com.company.kanban.integration;

import com.company.kanban.model.dto.TaskDTO;
import com.company.kanban.model.entity.Task;
import com.company.kanban.model.enums.Priority;
import com.company.kanban.model.enums.Status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {
                "kanban.dbUser=postgres",
                "kanban.dbPassword=postgres"
        }
)
public class WebSocketTests {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private WebSocketStompClient stompClient;
    private final String WEBSOCKET_TOPIC = "/topic/tasks";

    @BeforeEach
    public void setup() {
        List<Transport> transports = new ArrayList<>();
        transports.add(new WebSocketTransport(new StandardWebSocketClient()));
        SockJsClient sockJsClient = new SockJsClient(transports);

        this.stompClient = new WebSocketStompClient(sockJsClient);
        this.stompClient.setMessageConverter(new MappingJackson2MessageConverter());
    }
    @Test
    public void shouldReceiveWebSocketBroadcastAfterTaskPost() throws Exception {
        String wsUrl = "ws://localhost:" + port + "/ws";
        StompSession stompSession = stompClient.connectAsync(wsUrl, new StompSessionHandlerAdapter() {})
                .get(3, TimeUnit.SECONDS);

        CompletableFuture<Map<String, Object>> completableFuture = new CompletableFuture<>();
        stompSession.subscribe(WEBSOCKET_TOPIC, new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return Map.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                @SuppressWarnings("unchecked")
                Map<String, Object> notification = (Map<String, Object>) payload;
                completableFuture.complete(notification);
            }
        });

        Task taskToCreate = Task.builder()
                .title("Test WebSocket Task")
                .description("Testing WebSocket broadcast after POST")
                .status(Status.TO_DO)
                .priority(Priority.MED)
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Task> request = new HttpEntity<>(taskToCreate, headers);

        restTemplate.exchange("/api/tasks", HttpMethod.POST, request, TaskDTO.class);

        Map<String, Object> taskData = new HashMap<>();
        taskData.put("title", "Test WebSocket Task");
        taskData.put("description", "Testing WebSocket broadcast after POST");

        Map<String, Object> notification = new HashMap<>();
        notification.put("type", "CREATE");
        notification.put("task", taskData);

        stompSession.send(WEBSOCKET_TOPIC, notification);

        Map<String, Object> receivedNotification = completableFuture.get(5, TimeUnit.SECONDS);

        assertNotNull(receivedNotification);
        assertEquals("CREATE", receivedNotification.get("type"));

        @SuppressWarnings("unchecked")
        Map<String, Object> taskFromNotification = (Map<String, Object>) receivedNotification.get("task");
        assertEquals("Test WebSocket Task", taskFromNotification.get("title"));
        assertEquals("Testing WebSocket broadcast after POST", taskFromNotification.get("description"));
    }



}