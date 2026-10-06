package br.com.fiap.delivery.review.service;

import java.util.concurrent.atomic.LongAdder;

public class ReviewAccumulator {

    private final String dishName;
    private final LongAdder totalRatingSum = new LongAdder();
    private final LongAdder count = new LongAdder();

    public ReviewAccumulator(String dishName) {
        this.dishName = dishName;
    }

    public void addRating(int rating) {
        totalRatingSum.add(rating);
        count.increment();
    }

    public String getDishName() {
        return dishName;
    }

    public long getTotalRatingSum() {
        return totalRatingSum.sum();
    }

    public int getCount() {
        return (int) count.sum();
    }
}
