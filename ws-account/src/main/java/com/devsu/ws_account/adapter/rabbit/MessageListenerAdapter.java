package com.devsu.ws_account.adapter.rabbit;

import com.devsu.ws_account.adapter.rabbit.model.ClientDTO;
import com.devsu.ws_account.config.RabbitMQConfig;
import com.devsu.ws_account.config.exception.QueueException;
import com.devsu.ws_account.config.exception.SPError;
import com.devsu.ws_account.domain.AccountDomain;
import com.devsu.ws_account.domain.ClientDomain;
import com.devsu.ws_account.domain.service.RabbitMQAccountService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class MessageListenerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(MessageListenerAdapter.class);
    private final RabbitMQAccountService rabbitMQAccountService;

    public MessageListenerAdapter(RabbitMQAccountService rabbitMQAccountService) {
        this.rabbitMQAccountService = rabbitMQAccountService;
    }

    @Transactional
    @RabbitListener(queues = RabbitMQConfig.QUEUE_NAME)
    public void receiveClientInfo(ClientDTO clientDTO) {
        logger.info("Received message from RabbitMQ queue '{}': {}", RabbitMQConfig.QUEUE_NAME, clientDTO);
        try {
            ClientDomain clientDomain = clientDTO.toDomain();
            AccountDomain createdAccount = rabbitMQAccountService.createAccountForClient(clientDomain);
            logger.info("Account created successfully: {}", createdAccount);
        } catch (Exception e) {
            logger.error("Error processing message from RabbitMQ: {}", e.getMessage());
            throw new QueueException(SPError.RABBITMQ_RECEIVE_ERROR.getErrorCode(), SPError.RABBITMQ_RECEIVE_ERROR.getErrorMessage(), e);
        }
    }
}
