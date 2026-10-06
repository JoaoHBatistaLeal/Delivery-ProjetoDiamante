package br.com.fiap.delivery.order;

import br.com.fiap.delivery.order.client.PaymentClient;
import br.com.fiap.delivery.order.dto.OrderRequest;
import br.com.fiap.delivery.order.entity.Dish;
import br.com.fiap.delivery.order.exception.OutOfStockException;
import br.com.fiap.delivery.order.exception.PaymentProcessingException;
import br.com.fiap.delivery.order.repository.CustomerOrderRepository;
import br.com.fiap.delivery.order.repository.DishRepository;
import br.com.fiap.delivery.order.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.math.BigDecimal;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;

@SpringBootTest(properties = {
    "spring.ai.openai.api-key=test-key"
})
public class OrderServiceTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private DishRepository dishRepository;

    @Autowired
    private CustomerOrderRepository customerOrderRepository;

    @MockBean
    private PaymentClient paymentClient;

    @MockBean
    private ChatClient.Builder chatClientBuilder;

    @BeforeEach
    public void setup() {
        customerOrderRepository.deleteAll();
        Dish promoDish = dishRepository.findById(1L).orElse(null);
        if (promoDish != null) {
            promoDish.setStock(10);
            dishRepository.save(promoDish);
        } else {
            dishRepository.save(new Dish(1L, "House Burger", "Burger", new BigDecimal("29.90"), 10));
        }
    }

    @Test
    public void shouldHandle50ConcurrentRequestsWithExactStockDepletion() throws InterruptedException {
        doNothing().when(paymentClient).processPayment(any());

        int totalRequests = 50;
        ExecutorService executorService = Executors.newFixedThreadPool(totalRequests);
        CountDownLatch readyLatch = new CountDownLatch(totalRequests);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(totalRequests);

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger outOfStockCount = new AtomicInteger(0);

        for (int i = 0; i < totalRequests; i++) {
            executorService.submit(() -> {
                readyLatch.countDown();
                try {
                    startLatch.await();
                    orderService.createOrder(new OrderRequest(1L, 1));
                    successCount.incrementAndGet();
                } catch (OutOfStockException e) {
                    outOfStockCount.incrementAndGet();
                } catch (Exception ignored) {
                } finally {
                    doneLatch.countDown();
                }
            });
        }

        readyLatch.await();
        startLatch.countDown();
        doneLatch.await();
        executorService.shutdown();

        assertEquals(10, successCount.get());
        assertEquals(40, outOfStockCount.get());

        Dish promoDish = dishRepository.findById(1L).orElseThrow();
        assertEquals(0, promoDish.getStock());
    }

    @Test
    public void shouldRollbackStockWhenPaymentFails() {
        doThrow(new PaymentProcessingException("Payment processing failed")).when(paymentClient).processPayment(any());

        Dish initialDish = dishRepository.findById(1L).orElseThrow();
        int initialStock = initialDish.getStock();

        assertThrows(PaymentProcessingException.class, () -> {
            orderService.createOrder(new OrderRequest(1L, 1));
        });

        Dish afterDish = dishRepository.findById(1L).orElseThrow();
        assertEquals(initialStock, afterDish.getStock());
    }
}
