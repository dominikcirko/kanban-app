package com.company.kanban.service.implementations;

import com.company.kanban.service.interfaces.RateLimiterService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;

import jakarta.servlet.http.HttpServletRequest;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

@Service
public class RateLimiterServiceImpl implements RateLimiterService {

    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    private Bucket createNewBucket() {
        return Bucket.builder()
                .addLimit(limit -> limit
                        .capacity(100)
                        .refillGreedy(100, Duration.ofMinutes(1)))
                .build();
    }

    private String getClientIp() {
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attrs == null) return null;
        HttpServletRequest request = attrs.getRequest();
        return request.getRemoteAddr();
    }

    @Override
    public <T> ResponseEntity<T> performIfAllowed(Supplier<ResponseEntity<T>> supplier) {
        String clientIp = getClientIp();
        if (clientIp == null) {
            // fallback or deny
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        Bucket bucket = buckets.computeIfAbsent(clientIp, k -> createNewBucket());
        if (bucket.tryConsume(1)) {
            return supplier.get();
        } else {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
        }
    }
}