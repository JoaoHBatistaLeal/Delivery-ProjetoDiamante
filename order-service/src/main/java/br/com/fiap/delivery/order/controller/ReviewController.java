package br.com.fiap.delivery.order.controller;

import br.com.fiap.delivery.order.config.RabbitMQConfig;
import br.com.fiap.delivery.order.dto.ReviewMessage;
import br.com.fiap.delivery.order.dto.ReviewRequest;
import br.com.fiap.delivery.order.entity.Dish;
import br.com.fiap.delivery.order.exception.NotFoundException;
import br.com.fiap.delivery.order.exception.ValidationException;
import br.com.fiap.delivery.order.repository.DishRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/reviews")
@CrossOrigin(origins = "*")
public class ReviewController {

    private static final Logger log = LoggerFactory.getLogger(ReviewController.class);
    private final DishRepository dishRepository;
    private final RabbitTemplate rabbitTemplate;

    public ReviewController(DishRepository dishRepository, RabbitTemplate rabbitTemplate) {
        this.dishRepository = dishRepository;
        this.rabbitTemplate = rabbitTemplate;
    }

    @PostMapping
    public ResponseEntity<Void> submitReview(@RequestBody ReviewRequest request) {
        if (request == null || request.getRating() < 1 || request.getRating() > 5) {
            throw new ValidationException("Rating must be between 1 and 5");
        }

        if (request.getDishId() == null) {
            throw new NotFoundException("Dish not found");
        }

        Dish dish = dishRepository.findById(request.getDishId())
                .orElseThrow(() -> new NotFoundException("Dish not found"));

        ReviewMessage message = new ReviewMessage(
                dish.getId(),
                dish.getName(),
                request.getRating(),
                request.getComment()
        );

        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_NAME, RabbitMQConfig.ROUTING_KEY, message);
        log.info("Published review for dishId: {} with rating: {} to RabbitMQ", message.getDishId(), message.getRating());

        return ResponseEntity.accepted().build();
    }
}
