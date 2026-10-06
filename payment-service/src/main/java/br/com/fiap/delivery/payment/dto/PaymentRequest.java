package br.com.fiap.delivery.payment.dto;

import java.math.BigDecimal;

public class PaymentRequest {

    private BigDecimal amount;

    public PaymentRequest() {
    }

    public PaymentRequest(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
