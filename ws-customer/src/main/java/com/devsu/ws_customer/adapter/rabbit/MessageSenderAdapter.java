package com.devsu.ws_customer.adapter.rabbit;

import com.devsu.ws_customer.adapter.rabbit.model.ClientDTO;
import com.devsu.ws_customer.application.port.out.MessageSendRabbit;
import com.devsu.ws_customer.config.RabbitMQConfig;
import com.devsu.ws_customer.config.exception.QueueException;
import com.devsu.ws_customer.config.exception.SPError;
import com.devsu.ws_customer.domain.ClientDomain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class MessageSenderAdapter implements MessageSendRabbit {

    private static final Logger logger = LoggerFactory.getLogger(MessageSenderAdapter.class);
    private final RabbitTemplate rabbitTemplate;

    public MessageSenderAdapter(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    @Transactional
    public void sendClientInfo(ClientDomain clientDomain) {
        try {
            logger.info("Attempting to send client info to RabbitMQ: {}", clientDomain);
            ClientDTO clientDTO = ClientDTO.fromDomain(clientDomain);
            rabbitTemplate.convertAndSend(RabbitMQConfig.QUEUE_NAME, clientDTO);
            logger.info("Client info sent successfully to RabbitMQ");
        } catch (Exception e) {
            logger.error("Error sending client info to RabbitMQ: {}", e.getMessage());
            throw new QueueException(SPError.RABBITMQ_SEND_ERROR.getErrorCode(), SPError.RABBITMQ_SEND_ERROR.getErrorMessage(), e);
        }
    }

}
