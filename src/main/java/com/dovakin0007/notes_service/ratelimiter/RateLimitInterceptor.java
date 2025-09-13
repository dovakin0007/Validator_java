package com.dovakin0007.notes_service.ratelimiter;

import io.github.bucket4j.Bucket;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimitInterceptor implements HandlerInterceptor {

    private final Map<String, Bucket> ipBuckets = new ConcurrentHashMap<>();

    private Bucket createNewBucket() {
        return Bucket.builder()
                .addLimit(limit -> limit
                        .capacity(5) // max 5 requests
                        .refillGreedy(5, Duration.ofMinutes(1)) // refill 5 tokens every 1 min
                )
                .build();
    }

    private Bucket resolveBucket(String ip) {
        return ipBuckets.computeIfAbsent(ip, k -> createNewBucket());
    }

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) {

        String clientIp = request.getRemoteAddr();
        Bucket bucket = resolveBucket(clientIp);

        if (bucket.tryConsume(1)) {
            return true; // allow request
        } else {
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            return false; // reject request
        }
    }
}