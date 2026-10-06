package br.com.fiap.delivery.review.service;

import br.com.fiap.delivery.review.config.RabbitConfig;
import br.com.fiap.delivery.review.dto.ReviewMessage;
import br.com.fiap.delivery.review.entity.ReviewSummary;
import br.com.fiap.delivery.review.repository.ReviewSummaryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ReviewBufferService {

    private static final Logger log = LoggerFactory.getLogger(ReviewBufferService.class);
    private final ConcurrentHashMap<Long, ReviewAccumulator> buffer = new ConcurrentHashMap<>();
    private final ReviewSummaryRepository reviewSummaryRepository;

    public ReviewBufferService(ReviewSummaryRepository reviewSummaryRepository) {
        this.reviewSummaryRepository = reviewSummaryRepository;
    }

    @RabbitListener(queues = RabbitConfig.QUEUE_NAME)
    public void consumeReview(ReviewMessage message) {
        if (message == null || message.getDishId() == null) {
            return;
        }

        buffer.compute(message.getDishId(), (dishId, accumulator) -> {
            if (accumulator == null) {
                accumulator = new ReviewAccumulator(message.getDishName());
            }
            accumulator.addRating(message.getRating());
            return accumulator;
        });

        log.info("Received review from RabbitMQ for dish: {} with rating: {}", message.getDishName(), message.getRating());
    }

    @Scheduled(fixedRate = 5000)
    @Transactional
    public void flushBuffer() {
        if (buffer.isEmpty()) {
            return;
        }

        Map<Long, ReviewAccumulator> snapshot = new HashMap<>();
        for (Long dishId : buffer.keySet()) {
            ReviewAccumulator acc = buffer.remove(dishId);
            if (acc != null) {
                snapshot.put(dishId, acc);
            }
        }

        for (Map.Entry<Long, ReviewAccumulator> entry : snapshot.entrySet()) {
            Long dishId = entry.getKey();
            ReviewAccumulator acc = entry.getValue();

            ReviewSummary summary = reviewSummaryRepository.findById(dishId)
                    .orElse(new ReviewSummary(dishId, acc.getDishName(), 0L, 0, 0.0));

            long updatedTotalSum = summary.getTotalRatingSum() + acc.getTotalRatingSum();
            int updatedCount = summary.getCount() + acc.getCount();
            double updatedAverage = updatedCount > 0 ? (double) updatedTotalSum / updatedCount : 0.0;

            summary.setDishName(acc.getDishName());
            summary.setTotalRatingSum(updatedTotalSum);
            summary.setCount(updatedCount);
            summary.setAverage(Math.round(updatedAverage * 10.0) / 10.0);

            reviewSummaryRepository.save(summary);
        }

        log.info("Flushed {} reviews from buffer to database", snapshot.size());
    }
}
