package dev.andre.ResumeAiAnalysis.RabbitMQ;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class QueueConsumer {

    private final ExternalApiService externalApiService;

    public ExternalCallConsumer(ExternalApiService externalApiService) {
        this.externalApiService = externalApiService;
    }

    @RabbitListener(queues = RabbitConfig.QUEUE_MAIN, concurrency = "3-10")
    public void consume(MyRequestDto dto) {

        try {
            externalApiService.call(dto);
        } catch (Exception e) {
            throw new AmqpRejectAndDontRequeueException("Erro ao processar", e);
        }
    }
}
