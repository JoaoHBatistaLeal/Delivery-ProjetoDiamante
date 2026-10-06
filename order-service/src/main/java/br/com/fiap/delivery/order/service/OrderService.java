package br.com.fiap.delivery.order.service;

import br.com.fiap.delivery.order.client.PaymentClient;
import br.com.fiap.delivery.order.dto.OrderRequest;
import br.com.fiap.delivery.order.entity.CustomerOrder;
import br.com.fiap.delivery.order.entity.Dish;
import br.com.fiap.delivery.order.exception.NotFoundException;
import br.com.fiap.delivery.order.exception.OutOfStockException;
import br.com.fiap.delivery.order.exception.ValidationException;
import br.com.fiap.delivery.order.repository.CustomerOrderRepository;
import br.com.fiap.delivery.order.repository.DishRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrderService {

    private final DishRepository dishRepository;
    private final CustomerOrderRepository customerOrderRepository;
    private final PaymentClient paymentClient;

    public OrderService(DishRepository dishRepository,
                        CustomerOrderRepository customerOrderRepository,
                        PaymentClient paymentClient) {
        this.dishRepository = dishRepository;
        this.customerOrderRepository = customerOrderRepository;
        this.paymentClient = paymentClient;
    }

    public List<Dish> getAllDishes() {
        return dishRepository.findAll();
    }

    public Dish getDishById(Long id) {
        return dishRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Dish not found"));
    }

    public CustomerOrder getOrderById(Long id) {
        return customerOrderRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Order not found"));
    }

    @Transactional
    public CustomerOrder createOrder(OrderRequest request) {
        if (request == null || request.getQuantity() < 1) {
            throw new ValidationException("Quantity must be at least 1");
        }

        if (request.getDishId() == null) {
            throw new NotFoundException("Dish not found");
        }

        Dish dish = dishRepository.findByIdWithLock(request.getDishId())
                .orElseThrow(() -> new NotFoundException("Dish not found"));

        if (dish.getStock() < request.getQuantity()) {
            throw new OutOfStockException("Dish out of stock");
        }

        BigDecimal totalPrice = dish.getPrice().multiply(BigDecimal.valueOf(request.getQuantity()));

        dish.setStock(dish.getStock() - request.getQuantity());
        dishRepository.save(dish);

        paymentClient.processPayment(totalPrice);

        CustomerOrder order = new CustomerOrder(
                dish.getId(),
                request.getQuantity(),
                totalPrice,
                "CONFIRMED",
                LocalDateTime.now()
        );

        return customerOrderRepository.save(order);
    }
}
