package dev.andre.ResumeAiAnalysis.RabbitMQ;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import dev.andre.ResumeAiAnalysis.Config.RabbitMQConfig;
import dev.andre.ResumeAiAnalysis.Enums.ApplicationStatus;
import dev.andre.ResumeAiAnalysis.ImplementationAi.AIEntity;
import dev.andre.ResumeAiAnalysis.ImplementationAi.AiService;
import dev.andre.ResumeAiAnalysis.RabbitMQ.Dto.ProcessApplicationMessage;
import dev.andre.ResumeAiAnalysis.VacancyUser.UserVacancyService;
import java.io.File;
import java.io.IOException;

@Service
public class QueueConsumer {

    private final AiService aiService;
    private final UserVacancyService userVacancyService;

    public QueueConsumer(
            AiService aiService,
            UserVacancyService userVacancyService) {

        this.aiService = aiService;
        this.userVacancyService = userVacancyService;

    }

    @RabbitListener(queues = RabbitMQConfig.QUEUE_MAIN, ackMode = "MANUAL")
    public void consume(ProcessApplicationMessage msg, Channel channel, Message message) {

        long tag = message.getMessageProperties().getDeliveryTag();

        System.out.println("[CONSUMER] Mensagem recebida: " + msg);

        try {
            var userVacancy = userVacancyService.findById(msg.userVacancyId())
                    .orElseThrow(() -> new RuntimeException("UserVacancy não encontrado"));

            File file = new File(msg.filePath());
            System.out.println("[CONSUMER] Carregando PDF: " + file.getAbsolutePath());

            AIEntity aiEntity = aiService.gerarTextoComPdf(file, msg.vacancy(), userVacancy);

            System.out.println("[CONSUMER] AI gerado: " + aiEntity);

            aiEntity.setUserVacancy(userVacancy);
            userVacancy.setAiResponse(aiEntity);

            userVacancy.setStatus(ApplicationStatus.COMPLETED);
            userVacancyService.save(userVacancy);
            System.out.println("[CONSUMER] AI salvo no banco");

            System.out.println("[CONSUMER] Status atualizado para APPROVED");

            channel.basicAck(tag, false);

        } catch (Exception e) {
            e.printStackTrace(); // <-- AGORA VAMOS VER O ERRO REAL

            var userVacancy = userVacancyService.findById(msg.userVacancyId()).orElse(null);
            if (userVacancy != null) {
                userVacancy.setStatus(ApplicationStatus.REJECTED);
                userVacancyService.save(userVacancy);
            }

            try {
                channel.basicNack(tag, false, false); // <- agora compilador aceita
            } catch (IOException ioException) {
                ioException.printStackTrace(); // log do erro no nack
            }
        }
    }

}
