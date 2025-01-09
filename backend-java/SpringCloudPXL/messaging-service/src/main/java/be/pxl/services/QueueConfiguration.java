package be.pxl.services;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QueueConfiguration {
    @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
        return new RabbitAdmin(connectionFactory);
    }

    @Bean
    public Queue commentQueue() {
        return new Queue("commentQueue", true);
    }

    @Bean
    public Queue deleteCommentQueue() {
        return new Queue("deleteCommentQueue", true);
    }

    @Bean
    public Queue reviewQueue() {
        return new Queue("reviewQueue", true);
    }
}
