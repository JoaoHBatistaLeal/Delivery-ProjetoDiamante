package br.com.fiap.delivery.order.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@CrossOrigin(origins = "*")
public class HomeController {

    @GetMapping("/")
    public ResponseEntity<Map<String, Object>> getInfo() {
        return ResponseEntity.ok(Map.of(
                "service", "order-service",
                "status", "UP",
                "endpoints", Map.of(
                        "dishes", "GET /dishes",
                        "orders", "POST /orders",
                        "reviews", "POST /reviews",
                        "assistant", "POST /assistant"
                )
        ));
    }
}
