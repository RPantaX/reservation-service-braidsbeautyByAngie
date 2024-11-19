package com.braidsbeautyByAngie.adapters.handler;

import com.braidsbeautyByAngie.ports.in.ReservationServiceIn;
import com.braidsbeautyByAngie.ports.in.ServiceServiceIn;
import com.braidsbeautybyangie.sagapatternspringboot.aggregates.aggregates.commands.CancelReservationCommand;
import com.braidsbeautybyangie.sagapatternspringboot.aggregates.aggregates.commands.ReserveServiceCommand;
import com.braidsbeautybyangie.sagapatternspringboot.aggregates.aggregates.dto.ServiceCore;
import com.braidsbeautybyangie.sagapatternspringboot.aggregates.aggregates.events.ServiceReservationFailedEvent;
import com.braidsbeautybyangie.sagapatternspringboot.aggregates.aggregates.events.ServiceReservedEvent;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@KafkaListener(topics = "${services.commands.topic.name}")
@RequiredArgsConstructor
public class ServicesCommandsHandler {

    private final ReservationServiceIn service;
    private static final Logger logger = LoggerFactory.getLogger(ServicesCommandsHandler.class);
    private final KafkaTemplate<String, Object> kafkaTemplate;
    @Value("${services.events.topic.name}")
    private String servicesEventsTopicName;

    @KafkaHandler
    public void handleCommand(@Payload ReserveServiceCommand command) {
        try {
            List<ServiceCore>  serviceCoreList =  service.reserveReservationIn(command.getReservationId(), command.getShopOrderId());
            ServiceReservedEvent serviceReservedEvent = ServiceReservedEvent.builder()
                    .reservationId(command.getReservationId())
                    .shopOrderId(command.getShopOrderId())
                    .serviceList(serviceCoreList)
                    .build();
            kafkaTemplate.send(servicesEventsTopicName, serviceReservedEvent);
        } catch (Exception e) {
            logger.error("Error in ServicesCommandsHandler.handleCommand: {}", e.getMessage());
            ServiceReservationFailedEvent serviceReservationFailedEvent = ServiceReservationFailedEvent.builder()
                    .reservationId(command.getReservationId())
                    .shopOrderId(command.getShopOrderId())
                    .build();
            kafkaTemplate.send(servicesEventsTopicName, serviceReservationFailedEvent);
        }
    }

    @KafkaHandler
    public void handleCommand(@Payload CancelReservationCommand command) {

    }

}
