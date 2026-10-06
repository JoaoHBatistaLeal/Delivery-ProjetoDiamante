package br.com.fiap.delivery.order.limiter;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class OrderRateLimitInterceptor implements HandlerInterceptor {

    private static final int MAX_REQUESTS_PER_SECOND = 20;
    private final AtomicLong currentSecond = new AtomicLong(0);
    private final AtomicInteger counter = new AtomicInteger(0);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if ("POST".equalsIgnoreCase(request.getMethod())) {
            long currentSec = System.currentTimeMillis() / 1000;
            long recordedSec = currentSecond.get();

            if (currentSec != recordedSec) {
                if (currentSecond.compareAndSet(recordedSec, currentSec)) {
                    counter.set(0);
                }
            }

            if (counter.incrementAndGet() > MAX_REQUESTS_PER_SECOND) {
                response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                response.getWriter().write("{\"error\": \"Too many requests. Please try again later.\"}");
                return false;
            }
        }
        return true;
    }
}
