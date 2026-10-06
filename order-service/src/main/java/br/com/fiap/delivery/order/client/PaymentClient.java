package br.com.fiap.delivery.order.client;

import br.com.fiap.delivery.order.exception.PaymentProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.Map;

@Component
public class PaymentClient {

    private static final Logger log = LoggerFactory.getLogger(PaymentClient.class);
    private static final String PAYMENT_SERVICE_URL = "http://PAYMENT-SERVICE/payments";

    private final RestTemplate restTemplate;

    public PaymentClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Retryable(
        retryFor = Exception.class,
        maxAttempts = 4,
        backoff = @Backoff(delay = 500, multiplier = 2.0, maxDelay = 3000, random = true)
    )
    public void processPayment(BigDecimal amount) {
        log.info("Sending payment request for amount: {}", amount);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, BigDecimal>> request = new HttpEntity<>(Map.of("amount", amount), headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(PAYMENT_SERVICE_URL, request, Map.class);
        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new PaymentProcessingException("Payment processing failed");
        }
    }

    @Recover
    public void recover(Exception e, BigDecimal amount) {
        log.error("Payment retries exhausted for amount: {}", amount, e);
        throw new PaymentProcessingException("Payment processing failed");
    }
}
