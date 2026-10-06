package br.com.fiap.delivery.review.controller;

import br.com.fiap.delivery.review.dto.RankingResponse;
import br.com.fiap.delivery.review.repository.ReviewSummaryRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = "*")
public class RankingController {

    private final ReviewSummaryRepository reviewSummaryRepository;

    public RankingController(ReviewSummaryRepository reviewSummaryRepository) {
        this.reviewSummaryRepository = reviewSummaryRepository;
    }

    @GetMapping({"/", "/reviews"})
    public ResponseEntity<Map<String, Object>> getInfo() {
        return ResponseEntity.ok(Map.of(
                "service", "review-service",
                "status", "UP",
                "endpoints", Map.of("ranking", "GET /reviews/ranking")
        ));
    }

    @GetMapping("/reviews/ranking")
    public ResponseEntity<List<RankingResponse>> getRanking() {
        List<RankingResponse> ranking = reviewSummaryRepository.findAllByOrderByAverageDesc()
                .stream()
                .map(s -> new RankingResponse(s.getDishId(), s.getDishName(), s.getAverage(), s.getCount()))
                .toList();

        return ResponseEntity.ok(ranking);
    }
}
