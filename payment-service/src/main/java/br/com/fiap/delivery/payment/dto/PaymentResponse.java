package br.com.fiap.delivery.payment.dto;

public class PaymentResponse {

    private String status;
    private int instance;

    public PaymentResponse() {
    }

    public PaymentResponse(String status, int instance) {
        this.status = status;
        this.instance = instance;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getInstance() {
        return instance;
    }

    public void setInstance(int instance) {
        this.instance = instance;
    }
}
