package dev.andre.ResumeAiAnalysis.Config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE_MAIN = "exchange.main";
    public static final String EXCHANGE_RETRY = "exchange.retry";
    public static final String EXCHANGE_DLQ = "exchange.dlq";

    public static final String QUEUE_MAIN = "queue.main";
    public static final String QUEUE_RETRY = "queue.retry";
    public static final String QUEUE_DLQ = "queue.dlq";

    public static final String ROUTING_KEY_MAIN = "key.main";
    public static final String ROUTING_KEY_RETRY = "key.retry";
    public static final String ROUTING_KEY_DLQ = "key.dlq";

    @Bean
    public DirectExchange exchangeMain() {
        return new DirectExchange(EXCHANGE_MAIN);
    }

    @Bean
    public DirectExchange exchangeRetry() {
        return new DirectExchange(EXCHANGE_RETRY);
    }

    @Bean
    public DirectExchange exchangeDlq() {
        return new DirectExchange(EXCHANGE_DLQ);
    }

    @Bean
    public Queue queueMain() {
        return QueueBuilder.durable(QUEUE_MAIN)
                .withArgument("x-dead-letter-exchange", EXCHANGE_RETRY)
                .withArgument("x-dead-letter-routing-key", ROUTING_KEY_RETRY)
                .build();
    }

    @Bean
    public Queue queueRetry() {
        return QueueBuilder.durable(QUEUE_RETRY)
                .withArgument("x-dead-letter-exchange", EXCHANGE_MAIN)
                .withArgument("x-dead-letter-routing-key", ROUTING_KEY_MAIN)
                .withArgument("x-message-ttl", 5000) // espera 5s antes do retry
                .build();
    }

    @Bean
    public Queue queueDLQ() {
        return QueueBuilder.durable(QUEUE_DLQ).build();
    }

    @Bean
    public Binding bindingMain() {
        return BindingBuilder.bind(queueMain()).to(exchangeMain()).with(ROUTING_KEY_MAIN);
    }

    @Bean
    public Binding bindingRetry() {
        return BindingBuilder.bind(queueRetry()).to(exchangeRetry()).with(ROUTING_KEY_RETRY);
    }

    @Bean
    public Binding bindingDlq() {
        return BindingBuilder.bind(queueDLQ()).to(exchangeDlq()).with(ROUTING_KEY_DLQ);
    }
}

