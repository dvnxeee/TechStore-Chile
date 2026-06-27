package cl.techstore.api.service;

import cl.techstore.api.dto.AuditMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;

@Service
public class AuditProducer {

    private final SqsClient sqsClient;
    private final String queueUrl;
    private final ObjectMapper objectMapper;

    public AuditProducer(SqsClient sqsClient,
                         @Value("${app.sqs.audit-queue-url}") String queueUrl,
                         ObjectMapper objectMapper) {
        this.sqsClient = sqsClient;
        this.queueUrl = queueUrl;
        this.objectMapper = objectMapper;
    }

    public void enviarAuditoria(AuditMessage mensaje) {
        try {
            String json = objectMapper.writeValueAsString(mensaje);
            sqsClient.sendMessage(SendMessageRequest.builder()
                    .queueUrl(queueUrl)
                    .messageBody(json)
                    .build());
        } catch (Exception e) {
            throw new RuntimeException("Error al enviar mensaje de auditoría a SQS", e);
        }
    }
}
