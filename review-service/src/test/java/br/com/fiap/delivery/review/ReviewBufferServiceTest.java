package br.com.fiap.delivery.review;

import br.com.fiap.delivery.review.dto.RankingResponse;
import br.com.fiap.delivery.review.dto.ReviewMessage;
import br.com.fiap.delivery.review.entity.ReviewSummary;
import br.com.fiap.delivery.review.repository.ReviewSummaryRepository;
import br.com.fiap.delivery.review.service.ReviewBufferService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class ReviewBufferServiceTest {

    @Autowired
    private ReviewBufferService reviewBufferService;

    @Autowired
    private ReviewSummaryRepository reviewSummaryRepository;

    @BeforeEach
    public void setup() {
        reviewSummaryRepository.deleteAll();
    }

    @Test
    public void shouldAccumulateAndFlushReviewsCorrectly() {
        reviewBufferService.consumeReview(new ReviewMessage(1L, "House Burger", 5, "Great"));
        reviewBufferService.consumeReview(new ReviewMessage(1L, "House Burger", 4, "Good"));
        reviewBufferService.consumeReview(new ReviewMessage(2L, "Pizza Margherita", 3, "Ok"));

        reviewBufferService.flushBuffer();

        ReviewSummary burgerSummary = reviewSummaryRepository.findById(1L).orElseThrow();
        assertEquals(2, burgerSummary.getCount());
        assertEquals(9L, burgerSummary.getTotalRatingSum());
        assertEquals(4.5, burgerSummary.getAverage());

        ReviewSummary pizzaSummary = reviewSummaryRepository.findById(2L).orElseThrow();
        assertEquals(1, pizzaSummary.getCount());
        assertEquals(3L, pizzaSummary.getTotalRatingSum());
        assertEquals(3.0, pizzaSummary.getAverage());

        List<ReviewSummary> ranking = reviewSummaryRepository.findAllByOrderByAverageDesc();
        assertEquals(2, ranking.size());
        assertEquals(1L, ranking.get(0).getDishId());
        assertEquals(2L, ranking.get(1).getDishId());
    }
}
