package com.company.kanban.unit.service;

import com.company.kanban.controller.TaskController;
import com.company.kanban.service.implementations.RateLimiterServiceImpl;
import com.company.kanban.service.interfaces.RateLimiterService;
import com.company.kanban.service.interfaces.TaskService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;


@ExtendWith(MockitoExtension.class)
public class RateLimiterServiceTests {

    @Mock
    TaskService taskService;

    @Mock
    RateLimiterServiceImpl rateLimiterService;

    @InjectMocks
    TaskController taskController;

    @Test
    void testRateLimiting_allowsAndDenies() {
        int allowedRequests = 100;
        int requestsOverflow = 105;

        AtomicInteger counter = new AtomicInteger(0);
        Mockito.when(rateLimiterService.performIfAllowed(Mockito.any()))
                .thenAnswer(invocation -> {
                    if (counter.incrementAndGet() <= allowedRequests) {
                        Supplier<ResponseEntity<?>> supplier = invocation.getArgument(0);
                        return supplier.get();
                    } else {
                        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
                    }
                });

        for (int i = 0; i < allowedRequests; i++) {
            ResponseEntity<?> response = taskController.getAllTasks(null, null, 0, 10, null);
            Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        }

        for (int i = allowedRequests; i < requestsOverflow; i++) {
            ResponseEntity<?> response = taskController.getAllTasks(null, null, 0, 10, null);
            Assertions.assertEquals(HttpStatus.TOO_MANY_REQUESTS, response.getStatusCode());
        }
    }
}