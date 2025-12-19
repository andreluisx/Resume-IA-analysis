package dev.andre.ResumeAiAnalysis.RabbitMQ;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import dev.andre.ResumeAiAnalysis.Config.RabbitMQConfig;
import dev.andre.ResumeAiAnalysis.RabbitMQ.Dto.ProcessApplicationMessage;

@Component
public class QueueSender {

    private final RabbitTemplate rabbitTemplate;

    public QueueSender(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void send(ProcessApplicationMessage message) {
        rabbitTemplate.convertAndSend(RabbitMQConfig.QUEUE_MAIN, message);
    }

}