package br.com.fiap.delivery.order.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE_NAME = "delivery.exchange";
    public static final String QUEUE_NAME = "reviews.queue";
    public static final String ROUTING_KEY = "reviews.new";

    @Bean
    public TopicExchange deliveryExchange() {
        return new TopicExchange(EXCHANGE_NAME);
    }

    @Bean
    public Queue reviewsQueue() {
        return new Queue(QUEUE_NAME, true);
    }

    @Bean
    public Binding reviewsBinding(Queue reviewsQueue, TopicExchange deliveryExchange) {
        return BindingBuilder.bind(reviewsQueue).to(deliveryExchange).with(ROUTING_KEY);
    }

    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory,
                                         Jackson2JsonMessageConverter jackson2JsonMessageConverter) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jackson2JsonMessageConverter);
        return rabbitTemplate;
    }
}
