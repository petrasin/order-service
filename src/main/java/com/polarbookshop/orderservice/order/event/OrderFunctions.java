package com.polarbookshop.orderservice.order.event;

import com.polarbookshop.orderservice.order.domain.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Flux;

import java.util.function.Consumer;

@Configuration
public class OrderFunctions {

  private static final Logger log =
      LoggerFactory.getLogger(OrderFunctions.class);

  private final OrderService orderService;

  public OrderFunctions(OrderService orderService) {
    this.orderService = orderService;
  }

  @Bean
  public Consumer<Flux<OrderDispatchedMessage>> dispatchOrder() {
    return flux -> orderService.consumeOrderDispatchedEvent(flux)
                               .doOnNext(order -> log.info("The order with id" +
                                   " {} is " +
                                   "dispatched", order.id()))
                               .subscribe();

  }
}