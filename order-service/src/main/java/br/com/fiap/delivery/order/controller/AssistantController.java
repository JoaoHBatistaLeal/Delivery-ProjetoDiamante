package br.com.fiap.delivery.order.controller;

import br.com.fiap.delivery.order.dto.AssistantRequest;
import br.com.fiap.delivery.order.dto.AssistantResponse;
import br.com.fiap.delivery.order.service.AssistantService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/assistant")
@CrossOrigin(origins = "*")
public class AssistantController {

    private final AssistantService assistantService;

    public AssistantController(AssistantService assistantService) {
        this.assistantService = assistantService;
    }

    @PostMapping
    public ResponseEntity<AssistantResponse> ask(@RequestBody AssistantRequest request) {
        String answer = assistantService.ask(request.getQuestion());
        return ResponseEntity.ok(new AssistantResponse(answer));
    }
}
