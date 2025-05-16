package com.company.kanban.service.interfaces;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;

import java.util.function.Supplier;

public interface RateLimiterService {
    <T> ResponseEntity<T> performIfAllowed(Supplier<ResponseEntity<T>> supplier);
}
