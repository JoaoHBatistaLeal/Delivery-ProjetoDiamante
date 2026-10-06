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
        StringBuilder menuBuilder = new StringBuilder();

        for (Dish dish : dishes) {
            menuBuilder.append("- ")
                    .append(dish.getName())
                    .append(" | Preco: R$ ")
                    .append(dish.getPrice())
                    .append(" | Descricao: ")
                    .append(dish.getDescription())
                    .append(" | Estoque: ");

            if (dish.getStock() <= 0) {
                menuBuilder.append("ESGOTADO\n");
            } else {
                menuBuilder.append(dish.getStock()).append(" unidades\n");
            }
        }

        String systemPrompt = "Voce e o atendente virtual educado do restaurante. "
                + "Suas respostas devem ser sempre curtas, diretas e objetivas em portugues.\n\n"
                + "Diretrizes obrigatorias:\n"
                + "1. Utilize estritamente as informacoes do cardapio fornecido abaixo.\n"
                + "2. Se um prato estiver com estoque ESGOTADO, avise com clareza que o prato esta esgotado e jamais confirme pedidos ou recomende pratos sem estoque disponivel.\n"
                + "3. Caso o usuario faca perguntas fora do contexto do restaurante, pedidos, comidas e cardapio, ou envie mensagens ofensivas, recuse educadamente e redirecione o cliente para as opcoes do cardapio do restaurante.\n\n"
                + "Cardapio atualizado:\n"
                + menuBuilder;

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
