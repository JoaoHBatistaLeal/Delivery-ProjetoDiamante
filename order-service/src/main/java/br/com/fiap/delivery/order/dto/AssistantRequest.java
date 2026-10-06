package br.com.fiap.delivery.order.dto;

public class AssistantRequest {

    private String question;

    public AssistantRequest() {
    }

    public AssistantRequest(String question) {
        this.question = question;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }
}
