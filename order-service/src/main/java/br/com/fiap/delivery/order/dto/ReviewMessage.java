package br.com.fiap.delivery.order.dto;

public class ReviewMessage {

    private Long dishId;
    private String dishName;
    private int rating;
    private String comment;

    public ReviewMessage() {
    }

    public ReviewMessage(Long dishId, String dishName, int rating, String comment) {
        this.dishId = dishId;
        this.dishName = dishName;
        this.rating = rating;
        this.comment = comment;
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

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
