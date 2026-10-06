package br.com.fiap.delivery.order.service;

import br.com.fiap.delivery.order.entity.Dish;
import br.com.fiap.delivery.order.repository.DishRepository;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChatService {

    private final ChatClient chatClient;
    private final DishRepository dishRepository;

    public ChatService(ChatClient.Builder chatClientBuilder, DishRepository dishRepository) {
        this.chatClient = chatClientBuilder.build();
        this.dishRepository = dishRepository;
    }

    public String chat(String question) {
        List<Dish> dishes = dishRepository.findAll();
        StringBuilder menu = new StringBuilder();
        for (Dish dish : dishes) {
            menu.append("- ").append(dish.getName())
                .append(": R$ ").append(dish.getPrice())
                .append(", Stock: ").append(dish.getStock())
                .append(" (").append(dish.getDescription()).append(")\n");
        }

        String systemPrompt = "Cardapio:\n" + menu;

        return chatClient.prompt()
                .system(systemPrompt)
                .user(question)
                .call()
                .content();
    }

    public String ask(String question) {
        return chat(question);
    }
}
