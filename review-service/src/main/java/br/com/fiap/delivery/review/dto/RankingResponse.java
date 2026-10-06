package br.com.fiap.delivery.review.dto;

public class RankingResponse {

    private Long dishId;
    private String dishName;
    private double average;
    private int count;

    public RankingResponse() {
    }

    public RankingResponse(Long dishId, String dishName, double average, int count) {
        this.dishId = dishId;
        this.dishName = dishName;
        this.average = average;
        this.count = count;
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

    public double getAverage() {
        return average;
    }

    public void setAverage(double average) {
        this.average = average;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
