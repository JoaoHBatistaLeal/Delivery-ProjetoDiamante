package br.com.fiap.delivery.review.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "review_summaries")
public class ReviewSummary {

    @Id
    private Long dishId;

    private String dishName;
    private long totalRatingSum;
    private int count;
    private double average;

    public ReviewSummary() {
    }

    public ReviewSummary(Long dishId, String dishName, long totalRatingSum, int count, double average) {
        this.dishId = dishId;
        this.dishName = dishName;
        this.totalRatingSum = totalRatingSum;
        this.count = count;
        this.average = average;
    }

    public Long getDishId() {
        return dishId;
    }

    public void setDishId(Long dishId) {
        this.dishId = dishId;
    }

    public String getDishName() {
        return dishName;
    }

    public void setDishName(String dishName) {
        this.dishName = dishName;
    }

    public long getTotalRatingSum() {
        return totalRatingSum;
    }

    public void setTotalRatingSum(long totalRatingSum) {
        this.totalRatingSum = totalRatingSum;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public double getAverage() {
        return average;
    }

    public void setAverage(double average) {
        this.average = average;
    }
}
