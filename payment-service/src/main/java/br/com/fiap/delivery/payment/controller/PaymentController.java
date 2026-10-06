package br.com.fiap.delivery.payment.controller;

import br.com.fiap.delivery.payment.dto.PaymentRequest;
import br.com.fiap.delivery.payment.dto.PaymentResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.context.WebServerInitializedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Random;

@RestController
@CrossOrigin(origins = "*")
public class PaymentController implements ApplicationListener<WebServerInitializedEvent> {

    private static final Logger log = LoggerFactory.getLogger(PaymentController.class);
    private final Random random = new Random();
    private int port = 8081;

    @Override
    public void onApplicationEvent(WebServerInitializedEvent event) {
        this.port = event.getWebServer().getPort();
    }

    @GetMapping({"/", "/payments"})
    public ResponseEntity<Map<String, Object>> getInfo() {
        return ResponseEntity.ok(Map.of(
                "service", "payment-service",
                "status", "UP",
                "instance", port,
                "endpoints", Map.of("processPayment", "POST /payments")
        ));
    }

    @PostMapping("/payments")
    public ResponseEntity<PaymentResponse> processPayment(@RequestBody PaymentRequest request) {
        log.info("Payment processed by instance port: {}", port);
        System.out.println("Payment processed by instance: " + port);

        if (random.nextBoolean()) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        return ResponseEntity.ok(new PaymentResponse("APPROVED", port));
    }
}
