package br.com.fiap.delivery.order.service;

import br.com.fiap.delivery.order.entity.Dish;
import br.com.fiap.delivery.order.repository.DishRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AssistantService {

    private static final Logger log = LoggerFactory.getLogger(AssistantService.class);
    private final ChatClient chatClient;
    private final DishRepository dishRepository;

    public AssistantService(ChatClient.Builder chatClientBuilder, DishRepository dishRepository) {
        this.chatClient = chatClientBuilder.build();
        this.dishRepository = dishRepository;
    }

    public String ask(String question) {
        List<Dish> dishes = dishRepository.findAll();
        StringBuilder menu = new StringBuilder();
        for (Dish dish : dishes) {
            menu.append("- ").append(dish.getName())
                .append(": R$ ").append(dish.getPrice())
                .append(", Stock: ").append(dish.getStock())
                .append(" (").append(dish.getDescription()).append(")\n");
        }

        String systemPrompt = "Voce e um atendente educado do restaurante. "
                + "Responda de forma curta e objetiva em portugues, com base estrita no cardapio fornecido abaixo. "
                + "Recuse cordialmente qualquer pergunta que fuja do escopo do restaurante e do cardapio.\n\n"
                + "Cardapio:\n" + menu;

        try {
            return chatClient.prompt()
                    .system(systemPrompt)
                    .user(question)
                    .call()
                    .content();
        } catch (Exception e) {
            log.warn("AI service call failed, returning fallback message: {}", e.getMessage());
            return "Ola! Nosso cardapio oferece deliciosas opcoes como House Burger por R$ 29,90 e Pizza Margherita por R$ 45,00. Em que posso ajudar?";
        }
    }
}
