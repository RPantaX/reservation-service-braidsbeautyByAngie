package com.braidsbeautyByAngie.adapters.handler;

import com.braidsbeautyByAngie.ports.in.ReservationServiceIn;
import com.braidsbeautybyangie.sagapatternspringboot.aggregates.aggregates.commands.*;
import com.braidsbeautybyangie.sagapatternspringboot.aggregates.aggregates.dto.ReservationCore;
import com.braidsbeautybyangie.sagapatternspringboot.aggregates.aggregates.events.ServiceReservationFailedEvent;
import com.braidsbeautybyangie.sagapatternspringboot.aggregates.aggregates.events.ServiceReservedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;


@Component
@KafkaListener(topics = "${services.commands.topic.name}")
@RequiredArgsConstructor
@Slf4j
public class ServicesCommandsHandler {

    private final ReservationServiceIn service;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    @Value("${services.events.topic.name}")
    private String servicesEventsTopicName;

    @KafkaHandler
    public void handleCommand(@Payload ReserveServiceCommand command) {
        try {
            ReservationCore reservation = service.reserveReservationIn(command.getShopOrderId(), command.getReservationId());
            ServiceReservedEvent serviceReservedEvent = ServiceReservedEvent.builder()
                    .shopOrderId(command.getShopOrderId())
                    .reservationCore(reservation)
                    .productList(command.getProductList())
                    .build();
            kafkaTemplate.send(servicesEventsTopicName, serviceReservedEvent);
            log.info("Service reserved: {}", serviceReservedEvent);
        } catch (Exception e) {
            log.error("Error in ServicesCommandsHandler.handleCommand: {}", e.getMessage());
            ServiceReservationFailedEvent serviceReservationFailedEvent = ServiceReservationFailedEvent.builder()
                    .reservationId(command.getReservationId())
                    .productList(command.getProductList())
                    .shopOrderId(command.getShopOrderId())
                    .build();
            kafkaTemplate.send(servicesEventsTopicName, serviceReservationFailedEvent);
        }
    }

    @KafkaHandler
    public void handleCommand(@Payload CancelProductAndServiceReservationCommand command) {
        service.cancelReservationIn(command.getReservationId());

        ProductAndServiceCancelledToOrderEvent event = ProductAndServiceCancelledToOrderEvent.builder()
                .productList(command.getProductList())
                .shopOrderId(command.getShopOrderId())
                .reservationId(command.getReservationId())
                .build();
        try{
            log.info("Service cancelled: {}", event);
            kafkaTemplate.send(servicesEventsTopicName, event);
        } catch (Exception e) {
            log.error("Error in ServicesCommandsHandler.handleCommand: {}", e.getMessage());
        }

    }

}
