package br.com.fiap.delivery.order.dto;

public class ReviewRequest {

    private Long dishId;
    private int rating;
    private String comment;

    public ReviewRequest() {
    }

    public ReviewRequest(Long dishId, int rating, String comment) {
        this.dishId = dishId;
        this.rating = rating;
        this.comment = comment;
    }

    public Long getDishId() {
        return dishId;
    }

    public void setDishId(Long dishId) {
        this.dishId = dishId;
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
