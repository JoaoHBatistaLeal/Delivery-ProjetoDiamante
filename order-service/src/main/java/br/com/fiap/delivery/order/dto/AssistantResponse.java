package br.com.fiap.delivery.order.dto;

public class AssistantResponse {

    private String answer;

    public AssistantResponse() {
    }

    public AssistantResponse(String answer) {
        this.answer = answer;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }
}
